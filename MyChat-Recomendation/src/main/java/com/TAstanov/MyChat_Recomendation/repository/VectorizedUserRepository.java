package com.TAstanov.MyChat_Recomendation.repository;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.KnnSearch;
import co.elastic.clients.elasticsearch.core.*;
import co.elastic.clients.elasticsearch.core.search.Hit;
import co.elastic.clients.elasticsearch.indices.DeleteIndexRequest;
import co.elastic.clients.elasticsearch.indices.DeleteIndexResponse;
import com.TAstanov.MyChat_Recomendation.domain.user.GENDER;
import com.TAstanov.MyChat_Recomendation.domain.user.UserTemporalData;
import com.TAstanov.MyChat_Recomendation.domain.vector.VectorizedUser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;

@Repository
@RequiredArgsConstructor
@Slf4j
public class VectorizedUserRepository {

    private final ElasticsearchClient client;

    private static final String INDEX_MALE = "vectorized_users_male";
    private static final String INDEX_FEMALE = "vectorized_users_female";
    private static final String INDEX_OTHER = "vectorized_users_other";

    public void save(VectorizedUser user, UserTemporalData temporalData) {
        try {
            String indexName = getIndexName(user.getGender(), temporalData.getTemporaryTable(), false);

            // Преобразуем double[] в List<Float>
            List<Float> vectorAsList = DoubleStream.of(user.getVector())
                    .mapToObj(d -> (float) d)
                    .toList();

            Map<String, Object> doc = new HashMap<>();
            doc.put("userTag", user.getUserTag());
            doc.put("gender", user.getGender().toString());
            doc.put("vector", vectorAsList); // Теперь вектор передаём в виде списка

            IndexRequest<Map<String, Object>> request = IndexRequest.of(i -> i
                    .index(indexName)
                    .id(user.getUserTag())
                    .document(doc));
            client.index(request);
        } catch (IOException e) {
            log.error("Ошибка при сохранении пользователя с тегом {}: {}", user.getUserTag(), e.getMessage());
            log.error("Ошибка при сохранении пользователя", e);
            throw new RuntimeException("Ошибка при сохранении данных", e);
        }
    }


    public void dropIndex(GENDER gender, Integer shard) {
        String indexName = getIndexName(gender, shard, false);
        try {
            // Создаём запрос на удаление индекса
            DeleteIndexRequest request = DeleteIndexRequest.of(r -> r.index(indexName));
            // Отправляем запрос на удаление
            DeleteIndexResponse response = client.indices().delete(request);
            if (response.acknowledged()) {
                log.info("Индекс {} успешно удалён", indexName);
            } else {
                log.warn("Не удалось удалить индекс {}", indexName);
            }
        } catch (IOException e) {
            log.error("Ошибка при удалении индекса {}: {}", indexName, e.getMessage());
            throw new RuntimeException("Ошибка при удалении индекса " + indexName, e);
        }
    }

    public void truncateIndex(GENDER gender, Integer shard) {
        String indexName = getIndexName(gender, shard, false);
        try {
            DeleteByQueryRequest request = DeleteByQueryRequest.of(d -> d
                    .index(indexName)
                    .query(q -> q.matchAll(m -> m))
            );
            client.deleteByQuery(request);
            log.info("Индекс {} очищен", indexName);
        } catch (IOException e) {
            log.error("Ошибка при очистке индекса {}: {}", indexName, e.getMessage());
            throw new RuntimeException("Ошибка при очистке индекса", e);
        }
    }


    /**
     * @param gender Пол пользователя
     * @param opposite Если true, возвращает индекс для противоположного пола
     */
    private String getIndexName(GENDER gender, Integer shard, boolean opposite) {
        String result;
        if (!opposite) {
            if (gender == GENDER.MALE) {
                result = INDEX_MALE;
            } else if (gender == GENDER.FEMALE) {
                result = INDEX_FEMALE;
            } else {
                result = INDEX_OTHER;
            }
        } else {
            // При поиске противоположного пола: если не MALE, то ищем MALE, иначе FEMALE
            result = (gender != GENDER.MALE) ? INDEX_MALE : INDEX_FEMALE;
        }
        result += shard; // Используем shard вместо temporalData.getTemporaryTable()
        return result;
    }

    public List<VectorizedUser> findSimilarUsers(VectorizedUser vectorizedUser, UserTemporalData temporalData, Integer neighborsNumber) {
        try {
            String index = getIndexName(vectorizedUser.getGender(), temporalData.getTemporaryTable(), true);

            List<Float> queryVectorList = DoubleStream.of(vectorizedUser.getVector())
                    .mapToObj(d -> (float) d)
                    .toList();

            SearchRequest searchRequest = SearchRequest.of(s -> s
                    .index(index)
                    .size(400)
                    .knn(List.of(KnnSearch.of(knn -> knn
                            .field("vector")
                            .queryVector(queryVectorList)
                            .k(neighborsNumber)
                            .numCandidates(neighborsNumber * 2)
                    )))
            );

            SearchResponse<VectorizedUser> response = client.search(searchRequest, VectorizedUser.class);
            return response.hits().hits().stream().map(Hit::source).collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Ошибка при поиске схожих пользователей: {}", e.getMessage());
            throw new RuntimeException("Ошибка при поиске схожих пользователей", e);
        }
    }

    public VectorizedUser findByTag(String tag){
        return null;
    }

    public void saveAll(List<VectorizedUser> users, List<UserTemporalData> temporalDataList) {
        if (users.isEmpty() || temporalDataList.isEmpty()) {
            return;
        }

        try {
            // Группируем пользователей по индексам
            Map<String, List<IndexRequest<Map<String, Object>>>> requestsByIndex = new HashMap<>();

            for (int i = 0; i < users.size(); i++) {
                VectorizedUser user = users.get(i);
                UserTemporalData temporalData = temporalDataList.get(i);
                String indexName = getIndexName(user.getGender(), temporalData.getTemporaryTable(), false);

                // Преобразуем double[] в List<Float>
                List<Float> vectorAsList = DoubleStream.of(user.getVector())
                        .mapToObj(d -> (float) d)
                        .toList();

                Map<String, Object> doc = new HashMap<>();
                doc.put("userTag", user.getUserTag());
                doc.put("gender", user.getGender().toString());
                doc.put("vector", vectorAsList); // Теперь вектор передаем в виде списка

                // Создаем запрос на индексацию
                IndexRequest<Map<String, Object>> request = IndexRequest.of(iq -> iq
                        .index(indexName)
                        .id(user.getUserTag()) // Или используйте другой идентификатор, если это необходимо
                        .document(doc));

                // Группируем запросы по индексам
                requestsByIndex.computeIfAbsent(indexName, k -> new ArrayList<>()).add(request);
            }

            // Создаем BulkRequest
            BulkRequest.Builder bulkRequestBuilder = new BulkRequest.Builder();

            // Добавляем запросы на индексацию в BulkRequest
            requestsByIndex.forEach((indexName, requests) -> {
                requests.forEach(request -> {
                    bulkRequestBuilder.operations(b -> b.index(op -> op
                            .index(indexName)
                            .id(request.id())
                            .document(request.document())
                    ));
                });
            });


            BulkResponse response = client.bulk(bulkRequestBuilder.build());

            if (response.errors()) {
                log.warn("Некоторые документы не были сохранены в Elasticsearch!");
                response.items().forEach(item -> {
                    if (item.error() != null) {
                        log.warn("Ошибка при сохранении документа {}: {}", item.id(), item.error().reason());
                    }
                });
            } else {
                log.info("Успешно сохранены {} документов в Elasticsearch", users.size());
            }

        } catch (IOException e) {
            log.error("Ошибка при массовом сохранении пользователей: {}", e.getMessage());
            throw new RuntimeException("Ошибка при сохранении пользователей в Elasticsearch", e);
        }
    }



    private static BulkRequest.Builder getBuilder(Map<String, List<VectorizedUser>> usersByIndex) {
        BulkRequest.Builder bulkRequestBuilder = new BulkRequest.Builder();

        for (Map.Entry<String, List<VectorizedUser>> entry : usersByIndex.entrySet()) {
            String indexName = entry.getKey();
            List<VectorizedUser> userList = entry.getValue();

            for (VectorizedUser user : userList) {
                bulkRequestBuilder.operations(op -> op
                        .index(idx -> idx
                                .index(indexName)
                                .id(user.getUserTag())
                                .document(user)
                        )
                );
            }
        }
        return bulkRequestBuilder;
    }

}
