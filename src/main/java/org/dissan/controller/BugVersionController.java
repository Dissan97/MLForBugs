package org.dissan.controller;


import org.dissan.api.BugRetriever;
import org.dissan.api.BugVersion;
import org.dissan.bean.JiraBugBean;
import org.dissan.model.JiraBugModel;
import org.dissan.model.JiraTicketModel;
import org.json.JSONException;

import java.io.IOException;
import java.util.*;
import java.util.logging.Level;

/**
 * Class used to do proportion in Jira ticket -> P = (FV - IV) / (FV - OV) where FV != OV.
 * Index of releases must be used to retrieve correct FV.
 * Where FV must be sorted by their release date
 */

public class BugVersionController implements BugVersion {

        private static final String COLD_START_QUERIES = "coldStartQueries.json";
        private Map<String, JiraBugBean> coldStartCache = null;

        /**
         * Method used to retrieve projects to do cold start proportion method
         */
        public void coldStartProportion() throws JSONException, IOException {

                BugRetrieverController retriever;

                if (this.coldStartCache != null) {
                        return;
                }

                retriever = new BugRetrieverController();
                retriever.start(COLD_START_QUERIES);
                this.coldStartCache = retriever.getTicketBeanMap();

        }

        /**
         * @param issues ticket to calculate the proportion
         * @return proportion value
         */

        public double calculateProportion(List<JiraBugModel> issues) {
                int incremental = 0;
                Date openingDate;
                JiraTicketModel openingVersion;
                Date fixedDate;
                JiraTicketModel fixVersion;

                for (JiraBugModel ticketModel: issues){
                        openingDate = ticketModel.getOpeningVersion().getdDate();
                        openingVersion = ticketModel.getOpeningVersion();


                }
                return 0;
        }

        private void makeProportion(List<JiraBugModel> bugs, double proportion) {

        }

        @Override
        public void setupBugProportion(BugRetriever retriever) {
                Map<String, JiraBugBean> ticketBeanMap;
                //todo retrieve the bean and start proportions
                BugRetrieverController controller = (BugRetrieverController) retriever;
                ticketBeanMap = controller.getTicketBeanMap();
                double proportion = 0;



                for (Map.Entry<String, JiraBugBean> entry : ticketBeanMap.entrySet()) {
                        proportion = calculateProportion(entry.getValue().getBugWithIv());
                }

        }


}
