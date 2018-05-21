package models;

import javax.persistence.*;
import java.util.Objects;

@Entity(name = "Anchor")
@Table(name = "anchor")
public class Anchor {

    @Id @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "anchor_seq_generator")
    @Column(name = "id")
    @SequenceGenerator(name = "anchor_seq_generator", sequenceName = "anchor_id_seq", allocationSize = 1)
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Anchor anchor = (Anchor) o;
        return Objects.equals(getNetworkid(), anchor.getNetworkid());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getNetworkid());
    }
}
