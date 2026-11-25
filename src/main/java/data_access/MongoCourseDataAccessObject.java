package data_access;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.IndexOptions;
import com.mongodb.client.model.Indexes;
import com.mongodb.client.model.ReplaceOptions;
import com.mongodb.client.model.Sorts;
import entity.Course;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.util.ArrayList;
import java.util.List;

/**
 * DAO that stores each user's courses in MongoDB, one document per course in the
 * {@value #COLLECTION} collection.
 *
 * <p>This class is an Adapter: it turns the MongoDB driver's documents and collections into
 * the data access interfaces that the use cases already depend on, so no interactor changes.</p>
 */
public class MongoCourseDataAccessObject implements CourseDataAccess {

    public static final String COLLECTION = "courses";

    private final MongoCollection<Document> courses;

    /**
     * Creates the DAO and makes sure a user cannot have two courses with the same code.
     * @param database the connected database
     */
    public MongoCourseDataAccessObject(MongoDatabase database) {
        this.courses = database.getCollection(COLLECTION);
        courses.createIndex(
                Indexes.ascending(CourseDocumentMapper.USERNAME, CourseDocumentMapper.CODE_KEY),
                new IndexOptions().unique(true));
    }

    @Override
    public boolean existsByCode(String username, String courseCode) {
        return courseExists(username, courseCode);
    }

    @Override
    public boolean courseExists(String username, String courseCode) {
        return courseCode != null && courses.countDocuments(byCode(username, courseCode)) > 0;
    }

    @Override
    public Course getCourse(String username, String courseCode) {
        if (courseCode == null) {
            return null;
        }
        final Document document = courses.find(byCode(username, courseCode)).first();
        if (document == null) {
            return null;
        }
        return CourseDocumentMapper.toCourse(document);
    }

    /**
     * Saves the course, replacing the user's course with the same code if there is one.
     * A replaced course keeps its place in the enrolment order.
     */
    @Override
    public void saveCourse(String username, Course course) {
        courses.replaceOne(byCode(username, course.getCode()),
                CourseDocumentMapper.toDocument(username, course),
                new ReplaceOptions().upsert(true));
    }

    /**
     * Returns all of the user's courses in enrolment order.
     */
    @Override
    public List<Course> getCourses(String username) {
        final List<Course> result = new ArrayList<>();
        // A document's _id is an ObjectId, which starts with its creation time.
        for (Document document : courses.find(Filters.eq(CourseDocumentMapper.USERNAME, username))
                .sort(Sorts.ascending("_id"))) {
            result.add(CourseDocumentMapper.toCourse(document));
        }
        return result;
    }

    private static Bson byCode(String username, String courseCode) {
        return Filters.and(
                Filters.eq(CourseDocumentMapper.USERNAME, username),
                Filters.eq(CourseDocumentMapper.CODE_KEY, CourseDocumentMapper.key(courseCode)));
    }
}
