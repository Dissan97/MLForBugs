package org.dissan.bean;


import org.dissan.api.JiraBug;
import org.dissan.model.JiraBugModel;
import org.dissan.model.JiraTicketModel;

import java.util.ArrayList;
import java.util.List;

public class JiraBugBean implements JiraBug {
        private List<JiraBugModel> bugInfoList = new ArrayList<>();
        private final JiraTicketBean releasesBean;
        private final String projectName;
        private double averageProportion;

        public JiraBugBean(String projectName, JiraTicketBean releases) {
                this.projectName = projectName;
                this.releasesBean = releases;
        }

        public JiraBugBean(String projectName, List<JiraBugModel> bugBeans, JiraTicketBean releases) {
                this.projectName = projectName;
                this.bugInfoList = bugBeans;
                this.releasesBean = releases;
        }

        public void add(JiraBugModel jiraBugModel) {
                this.bugInfoList.add(jiraBugModel);
        }

        @Override
        public String getBugVersionInfo() {
                StringBuilder builder = new StringBuilder("bug info for: ");
                builder.append('[').append(this.projectName).append(']').append('\n');
                for (JiraBugModel b :
                        this.bugInfoList) {
                        builder.append(b.getKey()).append('{').append('\n').append('\t').append('[');
                        if (b.getInjectedVersion() != null) {
                                builder.append("\"iv\": ").append(b.getInjectedVersion().getVersionId()).append(", ");
                        }
                        builder.append("\"ov\": ").append(b.getOpeningVersion().getVersionId()).append(", \"fv\": ").append(b.getFixedVersion().getVersionId()).append(']').append('\n').append('}').append('\n');
                }
                return builder.toString();
        }

        public List<JiraBugModel> getBugWithIv() {
                List<JiraBugModel> jiraBugModelList = new ArrayList<>();

                for (JiraBugModel jb :
                        this.bugInfoList) {
                        if (jb.getInjectedVersion() != null) {
                                jiraBugModelList.add(jb);
                        }
                }
                return jiraBugModelList;
        }

        @Override
        public int getBugSize() {
                return this.bugInfoList.size();
        }

        public String getProjectName() {
                return this.projectName;
        }

        public String getReleases() {
                return this.releasesBean.getBugReleases();
        }

        public List<JiraTicketModel> getReleasesList() {
                return this.releasesBean.getReleases();
        }

        public List<JiraBugModel> getBugs() {
                return this.bugInfoList;
        }

        public double getAverageProportion() {
                return averageProportion;
        }

        public void setAverageProportion(double averageProportion) {
                this.averageProportion = averageProportion;
        }
}
