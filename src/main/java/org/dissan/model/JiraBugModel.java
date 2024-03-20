package org.dissan.model;


import org.jetbrains.annotations.NotNull;

import java.util.Date;
import java.util.List;
import java.util.Objects;

public class JiraBugModel {

    private final String key;
    private final JiraTicketModel fixedVersion;
    private final JiraTicketModel openingVersion;
    private JiraTicketModel injectedVersion = null;
    private List<JiraTicketModel> affectedVersion;

    public JiraBugModel(String key, JiraTicketModel ov, JiraTicketModel fv, List<JiraTicketModel> av) {
        this.key = key;
        this.openingVersion = ov;
        this.fixedVersion = fv;
        this.affectedVersion = av;
    }

    public void setInjectedVersion(@NotNull JiraTicketModel iv) {
        this.injectedVersion = iv;
    }

    public JiraTicketModel getInjectedVersion() {
        return this.injectedVersion;
    }

    public JiraTicketModel getOpeningVersion() {
        return openingVersion;
    }

    public JiraTicketModel getFixedVersion() {
        return this.fixedVersion;
    }



    public String getKey() {
        return this.key;
    }

    public List<JiraTicketModel> getAffectedVersion() {
        return affectedVersion;
    }

    public Date getFixedDate(){
        return this.fixedVersion.getdDate();
    }

    public void setAffectedVersion(@NotNull List<JiraTicketModel> affectedVersion) {
        this.affectedVersion = affectedVersion;
    }

    @Override
    public String toString() {
        return "JiraBugModel{" +
                "\"" + key + "\" {\n" +
                "\"fixedVersion\":" + fixedVersion +
                ", \"openingVersion\":" + openingVersion +
                ", \"injectedVersion\":" + injectedVersion +
                ", \"affectedVersion\":" + affectedVersion +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        JiraBugModel that = (JiraBugModel) o;
        return Objects.equals(key, that.key);
    }

}
