package models;

import javax.persistence.*;

@Entity(name = "Anchor")
@Table(name = "anchor")
public class Anchor {

    @Id
    @GeneratedValue
    @Column(name = "id")
    private long anchorId;
    private String networkid;

    public long getAnchorId() {
        return anchorId;
    }

    public void setAnchorId(final long anchorId) {
        this.anchorId = anchorId;
    }

    public String getNetworkid() {
        return networkid;
    }

    public void setNetworkid(String networkid) {
        this.networkid = networkid;
    }
}
