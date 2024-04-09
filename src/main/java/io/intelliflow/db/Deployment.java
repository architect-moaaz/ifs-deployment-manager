package io.intelliflow.db;

import io.quarkus.mongodb.panache.common.MongoEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;

import java.util.Date;

@MongoEntity(collection = "deployment")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Deployment {
    private ObjectId id;
	private String buildno;

	private String actionedby;

	private Date actiontime;


	private String comment;


	private String action;

	private String outcome;

}
