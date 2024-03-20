package org.dissan.bean;

import org.dissan.model.JiraTicketModel;

import java.util.ArrayList;
import java.util.List;

public class JiraTicketBean {
        private final String projectName;
        private final List<JiraTicketModel> jiraTicketModelList = new ArrayList<>();

        public JiraTicketBean(String projectName) {
                this.projectName = projectName;
        }

        public void addRelease(JiraTicketModel jiraTicketModel) {
                this.jiraTicketModelList.add(jiraTicketModel);
        }

        public List<JiraTicketModel> getReleases() {
                return jiraTicketModelList;
        }

        public String getBugReleases() {
                StringBuilder builder = new StringBuilder();
                builder.append('"').append(this.projectName).append('"').append(':').append('[').append('\n');
                int index = 1;
                for (JiraTicketModel b : this.getReleases()) {
                        builder.append('\t');
                        String end = "]\n";
                        if (index < this.getReleases().size()) {
                                end = ",\n";
                        }
                        builder.append(b).append(end);
                        index++;
                }
                return builder.toString();
        }
}
