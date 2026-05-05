package com.TAstanov.MyChat_Swipe.repository;


import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class AdjustingRepository {

    private final String URL = "jdbc:postgresql://localhost:5432/mychat?currentSchema=public";
    private final String USER = "postgres";
    private final String PASSWORD = "Rts28022007";

    public void updateAdjustedStatistics(Map<String, Double> usersStatisticsToUpdate){
        String sql = "UPDATE user_profiles SET beauty = ? WHERE tag = ?";
        try(Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
            PreparedStatement preparedStatement = connection.prepareStatement(sql)){

            for(Map.Entry<String, Double> entry: usersStatisticsToUpdate.entrySet()){
                preparedStatement.setDouble(1, entry.getValue());
                preparedStatement.setString(2, entry.getKey());

                preparedStatement.addBatch();
            }
            preparedStatement.executeBatch();
        }catch (Exception e){
            throw new RuntimeException();
        }
    }


    public Map<String, Double> getAdjustingValuesByTags(List<String> usersTags){
        String sql = "SELECT tag, beauty FROM user_profiles WHERE tag = ANY(?)";
        Map<String, Double> resultMap = new HashMap<>();
        try(Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
            PreparedStatement statement = connection.prepareStatement(sql)){
            Array sqlArray = connection.createArrayOf("text", usersTags.toArray());
            statement.setArray(1, sqlArray);

            ResultSet rs = statement.executeQuery();

            while(rs.next()){
                resultMap.put(rs.getString("tag"), rs.getDouble("beauty"));
            }

        } catch (Exception e){
            throw new RuntimeException();
        }
        return resultMap;
    }

}
