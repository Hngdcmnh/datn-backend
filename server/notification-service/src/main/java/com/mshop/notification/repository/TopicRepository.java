package com.mshop.notification.repository;

import com.google.cloud.firestore.Firestore;
import com.mshop.notification.repository.entity.Topic;
import org.springframework.stereotype.Repository;

@Repository
public class TopicRepository extends AbstractFirestoreRepository<Topic> {
    protected TopicRepository(Firestore firestore) {
        super(firestore, "topics");
    }
}
