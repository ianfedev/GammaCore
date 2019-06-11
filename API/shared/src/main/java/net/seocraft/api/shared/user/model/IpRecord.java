package net.seocraft.api.shared.user.model;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class IpRecord {
    private String number;
    private String country;
    private boolean primary;
}
