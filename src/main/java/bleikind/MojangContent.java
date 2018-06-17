package bleikind;

import bleikind.minecraft.PlayerProfile;
import bleikind.minecraft.SkinType;
import com.mashape.unirest.http.Unirest;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class MojangContent {

    private ConcurrentHashMap<String, Status> apiStatus = new ConcurrentHashMap<>();

    public MojangContent connect() {
        JSONObject obj = getJsonContent("https://status.mojang.com/check");
        obj.forEach((k, v) -> apiStatus.put((String) k, Status.valueOf((String) v)));
        return this;
    }

    public List<String> getServerBlacklist() {
        try {
            return Arrays.asList(Unirest.get("https://sessionserver.mojang.com/blockedservers").asString().getBody().split("\n"));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public Status getStatus(SType service) {
        if (service == null) return Status.UNKNOWN;
        return apiStatus.get(service.toString());
    }

    public String getUUIDOfUsername(String username) {
        return (String) getJsonContent("https://api.mojang.com/users/profiles/minecraft/" + username).get("id");
    }

    public String getUUIDOfUsername(String username, String timestamp) {
        return (String) getJsonContent("https://api.mojang.com/users/profiles/minecraft/" + username + "?at=" + timestamp).get("id");
    }

    public void updateSkin(String uuid, String token, SkinType skinType, String skinUrl) {
        try {
            Unirest.post("https://api.mojang.com/user/profile/" + uuid + "/skin").header("Authorization", "Bearer " + token).field("model", skinType.toString()).field("url", skinUrl).asString();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Map<String, Long> getNameHistoryOfPlayer(String uuid) {
        ConcurrentHashMap<String, Long> history = new ConcurrentHashMap<>();
        getJSONArray("https://api.mojang.com/user/profiles/" + uuid + "/names").forEach(o -> history.put((String) ((JSONObject) o).get("name"), ((JSONObject) o).get("changedToAt") == null ? 0 : Long.parseLong(((JSONObject) o).get("changedToAt").toString())));
        return history;
    }

    private static JSONArray getJSONArray(String url) {
        JSONArray arr = null;
        try {
            arr = (JSONArray) new JSONParser().parse(Unirest.get(url).asString().getBody());

        } catch (Exception e) {
            if (e instanceof ParseException) {
                throw new RuntimeException(e);
            } else if (e instanceof ClassCastException) {
                JSONObject obj = null;
                try {
                    obj = (JSONObject) new JSONParser().parse(Unirest.get(url).toString());
                } catch (Exception e1) { e1.printStackTrace(); }
                String err = (String) (obj.get("error"));
                if (err != null) {
                    switch (err) {
                        case "IllegalArgumentException":
                            throw new IllegalArgumentException((String) obj.get("errorMessage"));
                        default:
                            throw new RuntimeException();
                    }
                }
            }
        }
        return arr;
    }

    public PlayerProfile getPlayerProfile(String uuid) {
        JSONObject obj = getJsonContent("https://sessionserver.mojang.com/session/minecraft/profile/<uuid>");
        return new PlayerProfile(uuid, (String) obj.get("name"), (Set<PlayerProfile.Property>) ((JSONArray) obj.get("properties")).stream().map(o -> {
            PlayerProfile.Property p = new PlayerProfile.Property();
            JSONObject prop = (JSONObject) o; p.name = (String) prop.get("name"); p.signature = (String) prop.get("signature"); p.value = (String) prop.get("value");
            return p;
        }).collect(Collectors.toSet()));
    }

    public void resetSkin(String uuid, String token) {
        try {
            Unirest.delete("https://api.mojang.com/user/profile/" + uuid + "/skin").header("Authorization", "Bearer " + token).asString();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void uploadSkin(String uuid, String token, SkinType skinType, String file) {
        try {
            Unirest.put("https://api.mojang.com/user/profile/" + uuid + "/skin").header("Authorization", "Bearer " + token).field("model", skinType.toString().equals("") ? "alex" : skinType.toString()).field("file", file).asString();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public SalesStats getSaleStatistics(SalesStats.Options options) {
        JSONArray arr = new JSONArray();
        Collections.addAll(arr, options);

        SalesStats stats = null;
        try {
            JSONObject response = (JSONObject) new JSONParser().parse(Unirest.post("https://api.mojang.com/orders/statistics").field("metricKeys", arr).asString().getBody());
            stats = new SalesStats(Integer.valueOf((String) response.get("total")), Integer.valueOf((String) response.get("last24h")), Integer.valueOf((String) response.get("saleVelocityPerSeconds")));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return stats;
    }

    private static JSONObject getJsonContent(String url) {
        JSONObject obj;
        try {
            obj = (JSONObject) new JSONParser().parse(Unirest.get(url).asString().getBody());
            if (obj.get("error") != null) {
                switch ((String) obj.get("error")) {
                    case "IllegalArgumentException":
                        throw new IllegalArgumentException((String) obj.get("errorMessage"));
                    default:
                        throw new RuntimeException();
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return obj;
    }


}
