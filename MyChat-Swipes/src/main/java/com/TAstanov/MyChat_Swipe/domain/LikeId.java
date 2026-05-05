package com.TAstanov.MyChat_Swipe.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LikeId implements Serializable {

    private String userTag;
    private String likedUserTag;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LikeId likeId = (LikeId) o;
        return userTag.equals(likeId.userTag) && likedUserTag.equals(likeId.likedUserTag);
    }

    @Override
    public int hashCode() {
        return 31 * userTag.hashCode() + likedUserTag.hashCode();
    }
}

