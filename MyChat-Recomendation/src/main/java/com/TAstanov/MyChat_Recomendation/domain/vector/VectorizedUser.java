package com.TAstanov.MyChat_Recomendation.domain.vector;

import com.TAstanov.MyChat_Recomendation.domain.user.GENDER;
import lombok.Data;

@Data
public class VectorizedUser {

    private String userTag;
    private GENDER gender;
    private double[] vector;

}
