package org.dissan.controller;


import org.dissan.api.BugRetriever;
import org.dissan.bean.JiraBugBean;
import org.dissan.bean.JiraTicketBean;
import org.dissan.model.JiraBugModel;
import org.dissan.model.JiraTicketModel;
import org.dissan.utils.BugBuilder;
import org.dissan.utils.ConfigProjectsJson;
import org.dissan.utils.JSONet;
import org.dissan.utils.JiraApiRequest;
import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Class used to retrieve jira ticket information
 */

public class BugRetrieverController implements BugRetriever {
        private final Map<LocalDateTime, String> releaseNames = new HashMap<>();
        private final HashMap<LocalDateTime, String> releaseID = new HashMap<>();
        private final ArrayList<LocalDateTime> releases = new ArrayList<>();
        private final Map<String, JiraTicketBean> releaseBeanMap = new HashMap<>();
        private final Map<String, JiraBugBean> jiraTicketBeanMap = new HashMap<>();

        @Override
        public void start() throws IOException {
                this.start("conf.json");
        }

        @Override
        public void start(String conf) throws IOException {
                ConfigProjectsJson configProjectsJson = new ConfigProjectsJson(conf);
                List<JiraApiRequest> jiraApiRequests = configProjectsJson.getApiRequests();
                for (JiraApiRequest request : jiraApiRequests) {
                        this.doQuery(request);
                }
        }

        /**
         * This method does query to retrieve information about issues
         *
         * @param apiRequest Json file request are parsed and executed
         */
        private void doQuery(@NotNull JiraApiRequest apiRequest) throws IOException {
                int startIndex = apiRequest.getStart();
                int total = apiRequest.getTotal();
                int increment = 50;
                Map<String, JSONObject> fieldMap = new HashMap<>();
                for (; startIndex < total + increment; startIndex += increment) {
                        JSONObject jObject = JSONet.readJsonFromUrl(apiRequest.toString());
                        setupReleases(jObject.getJSONArray("issues"), fieldMap);
                        apiRequest.setStart(startIndex);
                }
                String projectName = apiRequest.getProjectName();
                this.setupBeans(projectName);
                //SETUP BUGS
                this.setupBugs(fieldMap, projectName);

                fieldMap.clear();
        }

        /**
         * This method is needed to set up releases and their relative bug
         *
         * @param issueArray issue array got from method query
         */
        private void setupReleases(@NotNull JSONArray issueArray, Map<String, JSONObject> fieldMap) {
                String kResolutionDate = "releaseDate";

                for (int index = 0; index < issueArray.length(); index++) {

                        JSONArray jaVersions = issueArray.getJSONObject(index).getJSONObject("fields").getJSONArray("versions");
                        for (int i = 0; i < jaVersions.length(); i++) {
                                String name = "";
                                String id = "";
                                if (jaVersions.getJSONObject(i).has(kResolutionDate)) {
                                        if (jaVersions.getJSONObject(i).has("name"))
                                                name = jaVersions.getJSONObject(i).getString("name");
                                        if (jaVersions.getJSONObject(i).has("id"))
                                                id = jaVersions.getJSONObject(i).get("id").toString();
                                        addRelease(jaVersions.getJSONObject(i).get(kResolutionDate).toString(), name, id);
                                }
                        }
                        releases.sort(LocalDateTime::compareTo);
                        JSONObject fields = issueArray.getJSONObject(index).getJSONObject("fields");
                        fieldMap.put(issueArray.getJSONObject(index).getString("key"), fields);
                }

        }

        private void setupBugs(@NotNull Map<String, JSONObject> fieldMap, String projectName) {

                Set<String> keys = fieldMap.keySet();
                System.out.println(keys.size());
                JiraBugBean bugBean = new JiraBugBean(projectName, this.releaseBeanMap.get(projectName));
                this.jiraTicketBeanMap.put(projectName, bugBean);

                for (String key : keys) {
                        JSONObject field = fieldMap.get(key);
                        JiraBugModel jiraBugModel = BugBuilder.getBugInstance(this.releaseBeanMap.get(projectName), field, key);
                        addBugToMap(projectName, jiraBugModel);
                }

                //Need to order the list of map -> by fixed date...

                this.jiraTicketBeanMap.get(projectName).getBugs().sort(Comparator.comparing(JiraBugModel::getFixedDate));


        }

        private void addBugToMap(String projectName, JiraBugModel jiraBugModel) {
                //todo add also bug that does not contain injected version
                if (jiraBugModel != null) {
                        if (jiraBugModel.getInjectedVersion() != null) {
                                if (!jiraBugModel.getFixedVersion().equals(jiraBugModel.getInjectedVersion()) || !jiraBugModel.getFixedVersion().equals(jiraBugModel.getOpeningVersion())) {
                                        this.jiraTicketBeanMap.get(projectName).add(jiraBugModel);
                                }
                        } else {
                                this.jiraTicketBeanMap.get(projectName).add(jiraBugModel);
                        }
                }
        }

        private void setupBeans(String projectName) {
                JiraTicketBean jiraTicketBean = new JiraTicketBean(projectName);
                int index = 1;
                for (LocalDateTime release : this.releases) {
                        String rID = this.releaseID.get(release);
                        String rName = this.releaseNames.get(release);
                        String rDate = release.toString();
                        JiraTicketModel ticketJira = new JiraTicketModel(rID, rName, rDate, index++);
                        jiraTicketBean.addRelease(ticketJira);
                }
                this.releaseBeanMap.put(projectName, jiraTicketBean);
        }

        private void addRelease(String strDate, String name, String id) {
                LocalDate date = LocalDate.parse(strDate);
                LocalDateTime dateTime = date.atStartOfDay();
                if (!releases.contains(dateTime))
                        releases.add(dateTime);
                releaseNames.put(dateTime, name);
                releaseID.put(dateTime, id);
        }

        //todo adjust this bean method
        public JiraTicketBean getJiraTicketBean(String projectName) {
                if (this.releaseBeanMap.containsKey(projectName)) {
                        return this.releaseBeanMap.get(projectName);
                }
                return null;
        }

        @Override
        public JiraBugBean getBugBean(String projectName) {
                if (this.jiraTicketBeanMap.containsKey(projectName)) {
                        return this.jiraTicketBeanMap.get(projectName);
                }
                return null;
        }

        /**
         * Method used to refresh all the data
         */

        public void close() {
                this.releaseBeanMap.clear();
                this.releaseID.clear();
                this.releaseNames.clear();
                this.releases.clear();
                this.jiraTicketBeanMap.clear();
        }

        public Map<String, JiraTicketBean> getReleaseBeanMap() {
                return releaseBeanMap;
        }

        public Map<String, JiraBugBean> getTicketBeanMap() {
                return jiraTicketBeanMap;
        }
}
