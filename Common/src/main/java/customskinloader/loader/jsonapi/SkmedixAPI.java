package customskinloader.loader.jsonapi;

import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import customskinloader.CustomSkinLoader;
import customskinloader.loader.JsonAPILoader;
import customskinloader.plugin.ICustomSkinLoaderPlugin;
import customskinloader.profile.ModelManager0;
import customskinloader.profile.UserProfile;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public class SkmedixAPI implements JsonAPILoader.IJsonAPI {

    public static class SkMedix extends JsonAPILoader.DefaultProfile {
        public SkMedix(JsonAPILoader loader) {
            super(loader);
        }

        @Override
        public String getName() {
            return "SkmedixAPI";
        }

        @Override
        public int getPriority() {
            return 500;
        }

        @Override
        public String getRoot() {
            return "https://sessionserver.skmedix.pl/profile/secure/";
        }

    }

    @Override
    public List<ICustomSkinLoaderPlugin.IDefaultProfile> getDefaultProfiles(JsonAPILoader loader) {
        return Lists.newArrayList(new SkMedix(loader));
    }

    public static UUID getOfflineUUID(String username) {
        return UUID.nameUUIDFromBytes(("OfflinePlayer:" + username).getBytes());
    }

    @Override
    public String toJsonUrl(String root, String username) {
        String uuid = String.valueOf(getOfflineUUID(username));
        String styledUuid = uuid.replace("-", "");
        String PREFIX = ".json";
        return root + styledUuid + PREFIX;
    }

    @Override
    public String getUserAgent() { return "sklauncher/3.2"; }

    @Override
    public UserProfile toUserProfile(String root, String json, boolean local) {
        try {
            SkmedixAPIResponse result = new Gson().fromJson(json, SkmedixAPIResponse.class);

            if (result == null || result.textures == null ||
                    !result.textures.containsKey(MinecraftProfileTexture.Type.SKIN))
                return null;

            return ModelManager0.toUserProfile(result.textures);

        } catch (Exception e) {
            // sk api returns html 404 response when profile not found
            return null;
        }
    }

    @Override
    public String getName() {
        return "SkmedixAPI";
    }

    public static class SkmedixAPIResponse {
        protected Map<MinecraftProfileTexture.Type, MinecraftProfileTexture> textures;
    }
}
