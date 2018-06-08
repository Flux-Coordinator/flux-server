package models;

import javax.persistence.*;
import java.util.Objects;

@Entity(name = "Anchor")
@Table(name = "anchor")
public class Anchor {

    @Id @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "anchor_seq_generator")
    @Column(name = "id")
    @SequenceGenerator(name = "anchor_seq_generator", sequenceName = "anchor_id_seq", allocationSize = 1)
    private Long anchorId;
    private String networkId;

    public Long getAnchorId() {
        return anchorId;
    }

    public void setAnchorId(final Long anchorId) {
        this.anchorId = anchorId;
    }

    public String getNetworkId() {
        return networkId;
    }

    public void setNetworkId(String networkId) {
        this.networkId = networkId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Anchor anchor = (Anchor) o;
        return Objects.equals(getNetworkId(), anchor.getNetworkId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getNetworkId());
    }
}
