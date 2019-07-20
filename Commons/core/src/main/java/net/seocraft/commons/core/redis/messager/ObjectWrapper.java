package net.seocraft.commons.core.redis.messager;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class ObjectWrapper<O> {
    private final O object;

    private final String serverSenderId;
}
