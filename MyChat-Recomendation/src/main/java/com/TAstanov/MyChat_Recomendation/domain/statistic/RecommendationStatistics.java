package com.TAstanov.MyChat_Recomendation.domain.statistic;

import com.TAstanov.MyChat_Recomendation.domain.user.UserPropertiesToVectorize;
import jakarta.persistence.*;
import java.lang.reflect.Field;
import lombok.Data;

@Entity
@Table(name = "recommendation_statistics")
@Data
public class RecommendationStatistics {

    @Id
    @Column(length = 10)
    private String gender; // 'Male' или 'Female'

    private Double avgHeight;
    private Long avgAge;
    private Double avgWeight;
    private Long avgEarnings;
    private Double avgBeauty;
    private Double avgPersonalityExtraversion;
    private Double avgPersonalityOpenness;
    private Double avgPersonalityConscientiousness;
    private Double avgLifeValueFamily;
    private Double avgLifeValueCareer;
    private Double avgActivityLevel;
    private Long number; // Количество пользователей в этой категории

    public void multiplyByNumber() {
        if (number == null || number == 0) {
            return;
        }

        for (Field field : this.getClass().getDeclaredFields()) {
            field.setAccessible(true);
            try {
                if ("number".equals(field.getName())) {
                    continue;
                }
                if (field.getType() == Double.class) {
                    Double value = (Double) field.get(this);
                    if (value != null) {
                        field.set(this, value * number);
                    }
                } else if (field.getType() == Integer.class) {
                    Integer value = (Integer) field.get(this);
                    if (value != null) {
                        field.set(this, value * number.intValue());
                    }
                } else if (field.getType() == Long.class) {
                    Long value = (Long) field.get(this);
                    if (value != null) {
                        field.set(this, value * number);
                    }
                }
            } catch (IllegalAccessException e) {
                throw new RuntimeException("Ошибка при обработке поля " + field.getName(), e);
            }
        }
    }

    public void add(UserPropertiesToVectorize userProperties) {
        if (userProperties == null) {
            return;
        }

        if (this.gender == null) {
            this.gender = userProperties.getGender().toString(); // Преобразуем в строку
        }

        // Пример добавления значений
        if(userProperties.getBeauty() ==null){
            userProperties.setBeauty(0);
        }
        this.avgHeight = (this.avgHeight == null ? 0 : this.avgHeight) + userProperties.getHeight();
        this.avgAge = (this.avgAge == null ? 0 : this.avgAge) + userProperties.getAge().intValue();  // Измененная строка
        this.avgWeight = (this.avgWeight == null ? 0 : this.avgWeight) + userProperties.getWeight().doubleValue();
        this.avgEarnings = (this.avgEarnings == null ? 0 : this.avgEarnings) + userProperties.getEarnings();
        this.avgBeauty = (this.avgBeauty == null ? 0 : this.avgBeauty) + userProperties.getBeauty().doubleValue();
        this.avgPersonalityExtraversion = (this.avgPersonalityExtraversion == null ? 0 : this.avgPersonalityExtraversion) + userProperties.getPersonalityExtraversion();
        this.avgPersonalityOpenness = (this.avgPersonalityOpenness == null ? 0 : this.avgPersonalityOpenness) + userProperties.getPersonalityOpenness();
        this.avgPersonalityConscientiousness = (this.avgPersonalityConscientiousness == null ? 0 : this.avgPersonalityConscientiousness) + userProperties.getPersonalityConscientiousness();
        this.avgLifeValueFamily = (this.avgLifeValueFamily == null ? 0 : this.avgLifeValueFamily) + userProperties.getLifeValueFamily();
        this.avgLifeValueCareer = (this.avgLifeValueCareer == null ? 0 : this.avgLifeValueCareer) + userProperties.getLifeValueCareer();
        this.avgActivityLevel = (this.avgActivityLevel == null ? 0 : this.avgActivityLevel) + userProperties.getActivityLevel();

        // Обновление number
        this.number = (this.number == null ? 0 : this.number) + 1;
    }


    public void divideByNumber() {
        if (number == null || number == 0) {
            return;
        }

        for (Field field : this.getClass().getDeclaredFields()) {
            field.setAccessible(true);
            try {
                if ("number".equals(field.getName())) {
                    continue;
                }
                if (field.getType() == Double.class) {
                    Double value = (Double) field.get(this);
                    if (value != null) {
                        field.set(this, value / number);
                    }
                } else if (field.getType() == Integer.class) {
                    Integer value = (Integer) field.get(this);
                    if (value != null) {
                        field.set(this, value / number);
                    }
                } else if (field.getType() == Long.class) {
                    Long value = (Long) field.get(this);
                    if (value != null) {
                        field.set(this, value / number);
                    }
                }
            } catch (IllegalAccessException e) {
                throw new RuntimeException("Ошибка при обработке поля " + field.getName(), e);
            }
        }
    }

    public static RecommendationStatistics averageMALE() {
        RecommendationStatistics stats = new RecommendationStatistics();

        // Средние показатели по России (данные могут быть примерными)
        stats.setGender("Male");  // Можно для примера взять только мужскую половину
        stats.setAvgHeight(175.0); // Средний рост мужчины в России
        stats.setAvgAge(33L); // Средний возраст
        stats.setAvgWeight(70.0); // Средний вес
        stats.setAvgEarnings(15000L); // Средняя зарплата в год в долларах (пример)
        stats.setAvgBeauty(5.0); // Средняя оценка внешности
        stats.setAvgPersonalityExtraversion(4.5); // Средняя экстраверсия
        stats.setAvgPersonalityOpenness(4.2); // Средняя открытость
        stats.setAvgPersonalityConscientiousness(4.7); // Средняя добросовестность
        stats.setAvgLifeValueFamily(7.5); // Важность семьи
        stats.setAvgLifeValueCareer(6.5); // Важность карьеры
        stats.setAvgActivityLevel(5.5); // Уровень активности
        stats.setNumber(10L); // Примерное количество пользователей

        return stats;
    }

    public static RecommendationStatistics averageFEMALE() {
        RecommendationStatistics stats = new RecommendationStatistics();

        // Средние показатели для женщин в России (данные могут быть примерными)
        stats.setGender("Female");  // Женский пол
        stats.setAvgHeight(164.0); // Средний рост женщины в России
        stats.setAvgAge(31L); // Средний возраст женщин в России
        stats.setAvgWeight(60.0); // Средний вес
        stats.setAvgEarnings(12000L); // Средняя зарплата в год для женщин в долларах (пример)
        stats.setAvgBeauty(6.0); // Средняя оценка красоты
        stats.setAvgPersonalityExtraversion(4.2); // Средняя экстраверсия
        stats.setAvgPersonalityOpenness(4.5); // Средняя открытость
        stats.setAvgPersonalityConscientiousness(4.8); // Средняя добросовестность
        stats.setAvgLifeValueFamily(8.0); // Важность семьи для женщин
        stats.setAvgLifeValueCareer(5.5); // Важность карьеры для женщин
        stats.setAvgActivityLevel(5.0); // Уровень активности
        stats.setNumber(10L); // Примерное количество пользователей

        return stats;
    }
}
