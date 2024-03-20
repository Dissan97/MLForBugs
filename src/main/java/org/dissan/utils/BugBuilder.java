package org.dissan.utils;


import org.dissan.bean.JiraTicketBean;
import org.dissan.model.JiraBugModel;
import org.dissan.model.JiraTicketModel;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.JSONArray;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.Logger;

/**
 * Static class that has no status it used to build model.JiraTicket
 */
public class BugBuilder {

        /**
         * It cannot be instantiated
         */
        private BugBuilder() {
        }

        /**
         * Method used to build jiraTicket list
         */
        public static JiraBugModel getBugInstance(@NotNull JiraTicketBean releases, @NotNull JSONObject fields, String key) {
                String releaseDate = fields.get("resolutiondate").toString();
                String creationDate = fields.get("created").toString();
                List<JiraTicketModel> ticketsJira = releases.getReleases();

                JiraTicketModel openingVersion = getVersionInfo(ticketsJira, creationDate);
                JiraTicketModel fixedVersion = getVersionInfo(ticketsJira, releaseDate);
                JiraTicketModel injectedVersion = null;
                List<JiraTicketModel> affectedVersions;
                JiraBugModel jiraBugModel = null;

                if (openingVersion != null && fixedVersion != null) {
                        if (openingVersion.getdDate().before(fixedVersion.getdDate()) || openingVersion.getdDate().equals(fixedVersion.getdDate())) {
                                affectedVersions = getAffectedVersion(fields.getJSONArray("versions"), ticketsJira);
                                if (!affectedVersions.isEmpty()) {
                                        Comparator<JiraTicketModel> ordering = Comparator.comparing(JiraTicketModel::getdDate);
                                        affectedVersions.sort(ordering);
                                        injectedVersion = affectedVersions.get(0);
                                }
                                jiraBugModel = new JiraBugModel(key, openingVersion, fixedVersion, affectedVersions);
                                if (injectedVersion != null) {
                                        jiraBugModel.setInjectedVersion(injectedVersion);
                                }
                        }
                }
                return jiraBugModel;
        }

        private static @NotNull List<JiraTicketModel> getAffectedVersion(@NotNull JSONArray fields, List<JiraTicketModel> ticketsJira) {
                List<JiraTicketModel> retListJiraTicketModel = new ArrayList<>();
                for (int i = 0; i < fields.length(); i++) {
                        for (JiraTicketModel jiraTicketModel : ticketsJira
                        ) {
                                if (fields.getJSONObject(i).getString("name").equals(jiraTicketModel.getVersionId())) {
                                        retListJiraTicketModel.add(jiraTicketModel);
                                }
                        }
                }

                return retListJiraTicketModel;
        }

        private static @Nullable JiraTicketModel getVersionInfo(@NotNull List<JiraTicketModel> jiraTicketModelList, String date) {

                Logger logger = Logger.getLogger(BugBuilder.class.getSimpleName() + ".getVersionInfo");
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
                Date dDate;

                try {
                        dDate = sdf.parse(date);
                        for (JiraTicketModel jiraTicketModel : jiraTicketModelList) {
                                if (dDate.before(jiraTicketModel.getdDate())) {
                                        return jiraTicketModel;
                                }
                        }
                } catch (ParseException e) {
                        logger.warning(e.getMessage());
                }


                return null;

        }
}
