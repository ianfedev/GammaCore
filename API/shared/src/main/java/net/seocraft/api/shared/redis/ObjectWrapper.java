package net.seocraft.api.shared.redis;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class ObjectWrapper<O> {
    private final O object;

    private final String serverSenderId;
}
