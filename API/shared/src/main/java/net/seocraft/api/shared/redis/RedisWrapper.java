package net.seocraft.api.shared.redis;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor @Getter
public class RedisWrapper<O> {
    String id;
    O object;
}
