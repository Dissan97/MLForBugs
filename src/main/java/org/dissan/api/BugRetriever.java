package org.dissan.api;




import org.dissan.bean.JiraBugBean;
import org.dissan.bean.JiraTicketBean;

import java.io.IOException;

public interface BugRetriever {
        void start() throws IOException;

        void close();

        void start(String conf) throws IOException;

        JiraTicketBean getJiraTicketBean(String projectName);

        JiraBugBean getBugBean(String projectName);
}
