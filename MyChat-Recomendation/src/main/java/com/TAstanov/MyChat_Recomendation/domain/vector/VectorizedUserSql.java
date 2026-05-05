package com.TAstanov.MyChat_Recomendation.domain.vector;

import com.TAstanov.MyChat_Recomendation.domain.user.GENDER;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "users_vectorized")
public class VectorizedUserSql {

    @Id
    private String userTag;
    private GENDER gender;
    private String vector;

    public double[] getVectorArray() {
        if (vector == null || vector.isEmpty()) {
            return new double[0];
        }
        String[] parts = vector.split(",");
        double[] array = new double[parts.length];
        for (int i = 0; i < parts.length; i++) {
            array[i] = Double.parseDouble(parts[i]);
        }
        return array;
    }

    // Метод для установки массива double[]
    public void setVectorArray(double[] array) {
        if (array == null || array.length == 0) {
            this.vector = "";
            return;
        }
        StringBuilder sb = new StringBuilder();
        for (double value : array) {
            sb.append(value).append(",");
        }
        // Убираем последнюю запятую
        this.vector = sb.substring(0, sb.length() - 1);
    }

}
