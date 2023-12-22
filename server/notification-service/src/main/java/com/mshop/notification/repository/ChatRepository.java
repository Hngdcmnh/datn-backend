package com.mshop.notification.repository;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import com.mshop.notification.repository.entity.Conversation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Repository
@Slf4j
public class ChatRepository extends AbstractFirestoreRepository<Conversation> {
    protected ChatRepository(Firestore firestore) {
        super(firestore, "conversations");
    }

    public List<Conversation> getConversation(String userId) {
        ApiFuture<QuerySnapshot> querySnapshotApiFuture = collectionReference.whereEqualTo("firstUserId", userId).get();
        try {
            List<QueryDocumentSnapshot> queryDocumentSnapshots = querySnapshotApiFuture.get().getDocuments();

            return queryDocumentSnapshots.stream()
                    .map(queryDocumentSnapshot -> queryDocumentSnapshot.toObject(parameterizedType))
                    .collect(Collectors.toList());

        } catch (InterruptedException | ExecutionException e) {
            log.error("Exception occurred while retrieving all document for {}", collectionName);
        }
        return Collections.emptyList();

    }
}
