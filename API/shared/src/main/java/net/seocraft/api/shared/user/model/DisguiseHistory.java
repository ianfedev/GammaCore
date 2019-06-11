package net.seocraft.api.shared.user.model;

import lombok.Getter;
import lombok.Setter;
import net.seocraft.api.shared.model.Group;

@Getter
@Setter
public class DisguiseHistory {
    private String nickname;
    private Group group;
    private String createdAt;
}