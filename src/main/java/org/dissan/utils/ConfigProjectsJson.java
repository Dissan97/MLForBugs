package org.dissan.utils;


import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ConfigProjectsJson {

    private static final String ME = ConfigProjectsJson.class.getSimpleName();
    private static final Logger LOGGER = Logger.getLogger(ME);

    private final List<JiraApiRequest> apiRequests = new ArrayList<>();

    /**
     * Method used to retrieve jira ticket data
     * @param confFile file where there is the projects target
     * @throws FileNotFoundException if some error on jira api error occurs
     */
    public ConfigProjectsJson(String confFile) throws FileNotFoundException {

        InputStream reader = new FileInputStream(confFile);
        JSONTokener jsonTokener = new JSONTokener(reader);
        JSONObject jsonObject = new JSONObject(jsonTokener);
        Set<String> projects = jsonObject.keySet();
        for (String project:
             projects) {

            String field = getFields(jsonObject.getJSONObject(project));
            JSONObject innerFields = jsonObject.getJSONObject(project);
            int start = innerFields.getInt("start");
            apiRequests.add(new JiraApiRequest(project, field, start));
        }

    }

    /**
     * Method used to parse jira field from json to string
     * @param jsonObject jsonObject used to parse jira field
     * @return return in string format the parsed field
     */

    private @NotNull String getFields(@NotNull JSONObject jsonObject) {
        StringBuilder retVal = new StringBuilder();
        try {
            JSONArray fields = jsonObject.getJSONArray("fields");
            int length = fields.length() - 1;
            retVal.append("%20&fields=");
            for (int i = 0; i < length; i++){
                retVal.append(fields.getString(i)).append(",");
            }
            retVal.append(fields.getString(length));
        }catch (JSONException e){
            LOGGER.log(Level.WARNING, e.getMessage());
        }
        return retVal.toString();
    }

    public List<JiraApiRequest> getApiRequests() {
        return apiRequests;
    }




}
