package fr.mosca421.worldprotector.util;

import com.google.gson.stream.JsonReader;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.Hand;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.UUID;

public final class PlayerUtils {

    public static boolean isSneaking(){
        return Screen.hasShiftDown();
    }

    public static boolean isHoldingCtrl(){
        return Screen.hasControlDown();
    }

    public static boolean isMainHand(Hand handIn){
        return Hand.MAIN_HAND == handIn;
    }


    public static class PlayerNameUtils{

        public static String queryUserinfo(String targetURL) {
            String playerName = "Z0rdak";

            HttpURLConnection connection = null;
            try {
                //Create connection
                URL url = new URL("https://api.mojang.com/users/profiles/minecraft/" + playerName);
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");

                connection.setUseCaches(false);
                connection.setDoOutput(true);

                //Send request
                DataOutputStream wr = new DataOutputStream (
                        connection.getOutputStream());
                wr.writeBytes("");
                wr.close();

                //Get Response
                InputStream is = connection.getInputStream();
                BufferedReader rd = new BufferedReader(new InputStreamReader(is));
                StringBuilder response = new StringBuilder(); // or StringBuffer if Java version 5+

                //JsonReader s = new JsonReader(rd);

                String line;
                while ((line = rd.readLine()) != null) {
                    response.append(line);
                    response.append('\r');
                }
                rd.close();
                return response.toString();
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
            }
        }

    }
}
