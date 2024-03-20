package org.dissan.model;



import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;

public class JiraTicketModel {
        private final String id;
        private final String versionId;
        private final String releaseDate;
        private final int index;

        public JiraTicketModel(String id, String versionId, String releaseDate, int index) {
                this.id = id;
                this.versionId = versionId;
                this.releaseDate = releaseDate;
                this.index = index;
        }

        public Date getdDate() {
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                Date d = null;
                try {
                        d = format.parse(this.releaseDate);
                } catch (ParseException e) {
                        //todo handle this execption

                }
                return d;
        }

        public String getVersionId() {
                return versionId;
        }

        public int getIndex() {
                return index;
        }

        @Override
        public String toString() {

                return "{\n\t\"index:\": " + this.index + ",\n\t\"id\":\"" + this.id + "\",\n\t\"versionId\":\"" + this.versionId + "\",\n\t\"releaseDate\":\"" + this.releaseDate + "\"\n}";
        }
}
