# University Learning Progress Tracker

A Java Swing desktop application that helps university students see how they are doing in each of their courses and where to focus their study time.

## What it does

A student signs up, logs in and lands on a home page listing their courses. From there they can:

- **Enrol in a course** with its code, name, target grade and main topics.
- **See their courses at a glance**: each course's estimated grade, target grade and whether they are on track.
- **Open a course's progress page**: estimated and target letter grades, and every topic marked green (confident), yellow (not so confident) or red (needs more attention).
- **Update their confidence** in any topic.
- **Calculate their current grade** from their test, assignment and exam marks and how much each is worth.
- **Set a target grade** and see the average they need on the rest of the course.
- **Rank their subjects by confidence**: all MAT courses together, all CSC courses together, and so on, from most to least confident.

Everything is saved per user in a database, so it is all still there next time.

## Use cases and team

Signup, login, logout and change password are reused from the starter code. Each new use case is one Clean Architecture engine.

| # | Use case | Trigger | What happens | Owner |
|---|---|---|---|---|
| 1 | Enrol in course | Enrol button on home | Checks the code, name, target grade and topics, rejects a course the user already has, and saves it | Member A |
| 2 | View enrolled courses | Home page is shown | Lists the user's courses with estimated grade, target grade and on-track status | Member A |
| 3 | View course progress | Choose a course on home | Shows that course's grades and topics on the progress page | Member B |
| 4 | Update topic confidence | Colour selector on a topic | Saves the new confidence level and shows it at once | Member B |
| 5 | Calculate current grade | Calculate in the grade calculator | Checks every mark and weight, saves them and shows the weighted grade | Member C |
| 6 | Set target grade | Target selector on progress page | Saves the new target and says what average is needed on the rest of the course | Member C |
| 7 | Rank subjects by confidence | Subject Ranking button on home | Groups courses by subject and ranks the subjects from most to least confident; ties share a rank | Member C |

Possible extensions: Add Topic, Drop Course.

## User stories

**Use cases 3 and 4: View and update course progress.** As a user, I can open one of my courses and update my confidence in its topics, so that I can see what I understand and which topics need more attention.
> Mia selects CSC207 on the home page. The progress page shows her estimated grade, target grade and every topic with its confidence colour. She now feels confident with Clean Architecture, so she changes it from yellow to green; the change is saved and the page shows it straight away.

**Use cases 5 and 6: Calculate my grade and set a target.** As a user, I can calculate my current grade from my marks and set a target grade, so that I know how I am doing and what average I need on the rest of the course.
> Mia opens the grade calculator for CSC207 and enters Midterm 1 (78%, worth 15%) and Assignment 1 (92%, worth 10%). The calculator shows a current grade of 83.6% (A-) based on 25% of the course, saves the marks, and her progress page now shows an estimated grade of A-. She then raises her target from A- to A, and the page tells her she needs an average of 85.5% on the remaining 75%, so she plans extra practice before the final exam.

**Use case 7: Rank subjects by confidence.** As a user, I can know which category of courses I am good at, so that I know where to spend my study time.
> Mia is enrolled in CSC207, CSC148, MAT137 and STA247. She presses Subject Ranking on the home page and sees Mathematics first at 83%, Computer Science second at 30% and Statistics last at 0%, so she spends the weekend on Statistics.

## Running the app

Users and courses are stored in **MongoDB**, either the team's shared MongoDB Atlas cloud database or a local server.

1. **Get a database.**
   - **MongoDB Atlas (shared by the team):** create a free M0 cluster at mongodb.com/atlas, add a database user, and under *Network Access* allow your IP (or `0.0.0.0/0` for the course). Copy the connection string from *Connect → Drivers*, which looks like `mongodb+srv://<user>:<password>@cluster0.xxxxx.mongodb.net/`.
   - **Local MongoDB (macOS):** `brew tap mongodb/brew && brew install mongodb-community && brew services start mongodb-community`. Nothing else is needed; the app uses `mongodb://localhost:27017` by default.
2. **Point the app at it (Atlas only).** In IntelliJ, open *Run → Edit Configurations → Main → Environment variables* and add `MONGODB_URI=<your connection string>`. `MONGODB_DATABASE` is optional (default `learning_tracker`). Never commit the connection string: it contains a password.
3. **Reload Maven** so the MongoDB driver is downloaded, then run `app.Main`. If the database cannot be reached, a "Database error" dialog appears after about 5 seconds.

## Architecture and design

Every use case follows the starter's login example:

```
View -> Controller -> InputData -> InputBoundary (Interactor)
                                        |  uses Entities and the DataAccessInterface
                                        v
View <- ViewModel <- Presenter <- OutputBoundary <- OutputData
```

**Dependency Rule.** Entities import nothing from other layers, use cases import only entities, interface adapters import only use cases, and views never import entities: presenters hand them plain strings. Business rules live in the entities, for example `Course` computes the weighted grade, the remaining weight, the average needed for the target and whether it is reached.

**SOLID.**
- *Interface Segregation:* each use case declares its own small data access interface with only the methods it needs.
- *Dependency Inversion:* interactors depend on those interfaces and on their output boundary, never on MongoDB or Swing.
- *Open/Closed:* a new feature is a new set of packages plus one `AppBuilder` method.

**Design patterns beyond the starter code.**
- **Strategy:** how topic confidence is scored for the subject ranking is a `ConfidenceScoringStrategy` in the entity layer. `StandardConfidenceScoring` counts green as 1, yellow as 0.5 and red as 0, and decides which colour an average counts as. `AppBuilder` injects it into `RankSubjectsInteractor`, so a different scoring rule is a new class, not a change to the interactor. `RankSubjectsInteractorTest` ranks the same courses with two strategies to show this.
- **Adapter:** `MongoCourseDataAccessObject` adapts the MongoDB driver's collections and documents to the use cases' data access interfaces, so moving from the in-memory DAO to MongoDB changed no use case. `CourseDocumentMapper` converts in both directions.
- **Decorator:** `CachingCourseDataAccessObject` wraps `MongoCourseDataAccessObject` and implements the same `CourseDataAccess` interface, so no use case knows it is there. The first read for a user loads all of that user's courses in one query; later reads come from memory, and every save is written through to MongoDB before the cache changes. It hands out copies, so an interactor that changes a course and then fails cannot leave an unsaved change in the cache. `AppBuilder` adds it with one line, and `CachingCourseDataAccessObjectTest` counts the calls that reach the database.

## Package structure

```
src/main/java
├── app/               Main; AppBuilder (creates every view and wires each use case)
├── entity/            User, UserFactory; Course, CourseFactory, Topic, Assessment;
│                      ConfidenceLevel (GREEN/YELLOW/RED); LetterGrade (A+..F);
│                      Subject (courses of one subject, average confidence);
│                      ConfidenceScoringStrategy, StandardConfidenceScoring
├── use_case/          signup, login, logout, change_password (starter);
│                      enrol_course, view_courses, view_course_progress, update_topic_confidence,
│                      calculate_grade, set_target_grade, rank_subjects
│                      each: InputBoundary, InputData, Interactor, OutputBoundary, OutputData,
│                            DataAccessInterface
├── interface_adapter/ ViewManagerModel; signup, login, logout, logged_in (starter);
│                      enrol_course, view_courses (CourseRow);
│                      course_progress (shared CourseProgressViewModel and State, TopicRow,
│                      AssessmentRow, TargetMessageFormatter, ViewCourseProgress controller/presenter);
│                      update_topic_confidence, calculate_grade, set_target_grade, rank_subjects
├── data_access/       MongoConfig (reads MONGODB_URI / MONGODB_DATABASE); MongoUserDataAccessObject;
│                      CourseDataAccess (all course DAO interfaces in one);
│                      MongoCourseDataAccessObject + CourseDocumentMapper;
│                      CachingCourseDataAccessObject (Decorator in front of the Mongo DAO);
│                      InMemoryCourseDataAccessObject, InMemoryUserDataAccessObject (tests);
│                      FileUserDataAccessObject (starter, no longer used)
└── view/              SignupView, LoginView, LabelTextPanel, ViewManager (starter);
                       LoggedInView (home: course list, Enrol, Subject Ranking); EnrolView;
                       CourseProgressView (grades, topic colours, Calculate Grade);
                       TargetGradePanel; GradeCalculatorDialog; SubjectRankingDialog
```

A use case that needs course data adds its data access interface to the `extends` list of `CourseDataAccess` (which `MongoCourseDataAccessObject` and `CachingCourseDataAccessObject` implement) and to the `implements` list of `InMemoryCourseDataAccessObject`.

## Data persistence

- **`users` collection:** one document per user, `{"_id": "mia", "password": "..."}`. Passwords are plain text, as in the starter code; hashing them would need a change to the login and change-password interactors.
- **`courses` collection:** one document per course. A unique index on `(username, codeKey)` stops a user enrolling in the same course twice.

```json
{
  "username": "mia",
  "codeKey": "CSC207",
  "code": "CSC207",
  "name": "Software Design",
  "targetGrade": "A",
  "topics": [{"name": "Clean Architecture", "confidence": "YELLOW"}],
  "assessments": [{"name": "Midterm 1", "mark": 78.0, "weight": 15.0}]
}
```

## Testing and code quality

- **Tests (`mvn test`):**
  - Every use case has an interactor test written like `SignupInteractorTest`: build the input data, use `InMemoryCourseDataAccessObject` and a fake presenter that asserts on the output data, then run the interactor.
  - Entities, presenters that format text, and controllers have their own tests.
  - `CourseDocumentMapperTest` checks the MongoDB document format without a server.
  - `MongoDataAccessObjectTest` runs the DAOs against a real server in a separate `learning_tracker_test` database, deleted afterwards. It is skipped when no server is reachable.
- **Checkstyle (`mvn checkstyle:check`, also part of `mvn verify`):**
  - The rules are in `config/checkstyle/checkstyle.xml` and match the starter's style: 4-space indentation, package names such as `use_case`, lines up to 120 characters.
  - It checks imports, naming, magic numbers, method length and complexity, and missing Javadoc.
  - Problems are reported as warnings and do not fail the build. The few that remain are deliberate: output-data constructors with many parameters (immutable data carriers) and the long constructors of the starter's views.
- **Workflow:**
  - Each use case is built on its own branch and merged through a pull request that includes its tests and is reviewed by another member before merging.
  - Shared files (`Main`, `AppBuilder`, entities, data access objects) only gain one method or one `implements` entry per use case, which keeps pull requests from colliding.
