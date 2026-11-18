# University Learning Progress Tracker

A Java Swing desktop application that lets university students track how they are doing in each course they are enrolled in.

Status: planning. This document is the agreed scaffold; the new use cases are not implemented yet.

## Program overview

A student signs up, logs in, and lands on a home page listing their enrolled courses. From there they can:

- Enrol in a course by pressing Enrol and entering the course details (code, name, target grade, main topics/chapters).
- Open a course's learning progress page, which shows:
  - the estimated grade and the target grade as letter grades (A, B, C, D, ...);
  - the list of main topics/chapters, each marked green (confident), yellow (not so confident), or red (needs more attention).
- Calculate their current grade in a small pop-up grade calculator, by entering test/assignment/exam marks and how much each one weighs.
- See which subjects they are most confident in: courses are grouped by subject (every MAT course, every CSC course, ...) and the subjects are ranked by the average confidence of their topics.

Data is saved per user, so courses, topic confidence and marks are still there after the program is restarted.

## Use cases and team split

Signup, login, logout and change password come from the starter code and are reused. The new use cases below are each one CA engine.

| # | Use case | Trigger | What the interactor does | Owner |
|---|---|---|---|---|
| 1 | Enrol in course | Enrol button on home | Validates code, name, target grade and topic list; rejects duplicates; saves a new Course | Member A |
| 2 | View enrolled courses | Home page shown after login | Loads the user's courses with estimated vs target grade for the home list | Member A |
| 3 | View course progress | Click a course on home | Loads one course's grades and topics and switches to the progress page | Member B |
| 4 | Update topic confidence | Green/yellow/red selector on a topic | Sets the topic's confidence level and saves | Member B |
| 5 | Calculate current grade | Calculate in the pop-up | Validates marks and weights, computes the weighted grade, saves the assessments, updates the estimated grade | Member C |
| 6 | Set target grade | Edit target on progress page | Updates the target letter grade and reports the average needed on the remaining weight | Member C |
| 7 | Rank subjects by confidence | Subject Ranking button on home | Groups the user's courses by subject code (the letters of the course code, e.g. MAT), averages the confidence of every topic in each subject (green 100%, yellow 50%, red 0%), and ranks the subjects from most to least confident; tied subjects share a rank | Member C |

Each member owns one screen area (home, progress page, calculator pop-up), which keeps pull requests from colliding. Possible replacements or future extensions: Add Topic, Drop Course.

Shared by everyone: Main, AppBuilder, the entities and factories, and the data access objects.

## User story: Rank subjects by confidence (use case 7)

As a user, I can know which category of courses I am good at, so that I know where to spend my study time.

Mia is enrolled in CSC207, CSC148, MAT137 and STA247. On the home page she presses Subject Ranking. The app groups her courses by subject (every MAT course together, every CSC course together, and so on) and ranks the subjects by her average confidence across all their topics, where a green topic counts as 100%, yellow as 50% and red as 0%. She sees Mathematics first at 83%, Computer Science second at 30% and Statistics last at 0%, so she decides to spend this weekend on Statistics.

## User story: View and update course progress (use cases 3 and 4)

As a user, I can open one of my enrolled courses and update my confidence in its topics, so that I can see what I understand and which topics need more attention.

Mia selects CSC207 on the home page. The app opens the course progress page and shows her estimated grade, target grade and all the course topics with their current confidence colours. After reviewing the list, she changes Clean Architecture from yellow to green because she now feels confident with it. The app saves the change, and the progress page immediately shows the updated green confidence indicator.

## Package structure

```
src/main/java
├── app/
│   ├── Main.java                      (shared; chain grows per use case)
│   └── AppBuilder.java                (shared; one addXView / addXUseCase method each)
├── entity/
│   ├── User.java, UserFactory.java    (reused as-is)
│   ├── Course.java                    code, name, targetGrade, topics, assessments
│   ├── CourseFactory.java
│   ├── Topic.java                     name + ConfidenceLevel
│   ├── ConfidenceLevel.java           constants GREEN, YELLOW, RED; fromName(), getScore()
│   ├── Subject.java                   a subject code and its courses; average confidence
│   ├── Assessment.java                name, mark, weight
│   └── LetterGrade.java               constants A+..F; fromPercentage(), fromLabel()
├── use_case/
│   ├── signup/ login/ logout/ change_password/      (existing)
│   ├── enrol_course/
│   ├── view_courses/
│   ├── view_course_progress/
│   ├── update_topic_confidence/
│   ├── calculate_grade/
│   ├── set_target_grade/
│   └── rank_subjects/
│        each: XInputBoundary, XInputData, XInteractor,
│              XOutputBoundary, XOutputData, XCourseDataAccessInterface
├── interface_adapter/
│   ├── ViewManagerModel.java          (existing)
│   ├── home/              HomeViewModel, HomeState          (replaces logged_in)
│   ├── enrol_course/      Controller, Presenter, EnrolViewModel, EnrolState
│   ├── view_courses/      Controller, Presenter
│   ├── course_progress/   CourseProgressViewModel, CourseProgressState,
│   │                      ViewCourseProgress Controller + Presenter
│   ├── update_topic_confidence/   Controller, Presenter
│   ├── calculate_grade/   Controller, Presenter, GradeCalculatorViewModel, State
│   ├── set_target_grade/  Controller, Presenter
│   └── rank_subjects/     Controller, Presenter, SubjectRankingViewModel, State
├── data_access/
│   ├── FileUserDataAccessObject.java  (existing)
│   ├── DBCourseDataAccessObject.java  (API-backed; implements all six course interfaces)
│   └── InMemoryCourseDataAccessObject.java  (for interactor tests)
└── view/
    ├── SignupView, LoginView, ViewManager, LabelTextPanel   (existing)
    ├── HomeView.java                  course list + Enrol button (from LoggedInView)
    ├── EnrolView.java                 course details form
    ├── CourseProgressView.java        estimated/target grade, topic list with colour selectors
    ├── GradeCalculatorDialog.java     JDialog pop-up
    └── SubjectRankingDialog.java      JDialog pop-up with the subject ranking

src/test/java/use_case/<each use case>/XInteractorTest.java
```

## The CA engine for each use case

Every use case follows the same flow as the existing login example:

```
View -> Controller -> InputData -> InputBoundary (Interactor)
                                        |  uses Entities and the DataAccessInterface
                                        v
View <- ViewModel <- Presenter <- OutputBoundary <- OutputData
```

All dependencies point inward: entities import nothing from other layers, use cases import only entities, and interface adapters import only use cases.

## External API and data persistence

Planned: the course-provided API used by DBUserDataAccessObject on the db-user branch of the starter repo, storing each user's courses as one JSON object. This would satisfy both the external API and the persistence requirements.

To confirm before building on it: the API's endpoints and whether it can store the nested course/topic/assessment data.

Planned JSON shape per user:

```json
{
  "courses": [
    {
      "code": "CSC207",
      "name": "Software Design",
      "targetGrade": "A",
      "topics": [{"name": "Clean Architecture", "confidence": "YELLOW"}],
      "assessments": [{"name": "Midterm 1", "mark": 78.0, "weight": 15.0}]
    }
  ]
}
```

## Integration notes for use cases 5, 6 and 7

Use cases 5 (Calculate current grade), 6 (Set target grade) and 7 (Rank subjects by confidence) are implemented and wired in AppBuilder (addCalculateGradeUseCase, addSetTargetGradeUseCase, addRankSubjectsUseCase). To try them before the progress page exists, run src/test/java/app/GradeToolsDemo.java from the IDE.

What the other use cases need to do to connect them:

Course progress page (use case 3, Member B). interface_adapter/course_progress/ holds a minimal CourseProgressViewModel and CourseProgressState, which both presenters update. Extend these rather than creating a second view model. When a course is opened, fill the state's username, courseCode, targetGrade, estimatedGrade and assessments (the grade calculator starts from these rows). In CourseProgressView, add appBuilder.getTargetGradePanel() and make the Calculate Grade button call appBuilder.getGradeCalculatorDialog().open().

Home page (Member A). Add a Subject Ranking button to HomeView that calls appBuilder.getSubjectRankingDialog().open(username) with the logged-in username.

Course DAO. AppBuilder uses InMemoryCourseDataAccessObject for now, so courses are lost on restart. When DBCourseDataAccessObject is written, add CalculateGradeCourseDataAccessInterface and SetTargetGradeCourseDataAccessInterface (both getCourse(username, courseCode) and saveCourse(username, course)) and RankSubjectsCourseDataAccessInterface (getCourses(username)) to its implements list, and switch the field marked TODO in AppBuilder.

Letter grades. The target grade selector reads CourseProgressViewModel.GRADE_OPTIONS, a copy of the LetterGrade labels so views do not import entities. TargetMessageFormatterTest fails if the two lists drift apart.

## Integration notes for use cases 3 and 4

Course progress page (use case 3, Member B). Use the existing view_course_progress controller, presenter, interactor, CourseProgressViewModel and CourseProgressState. When a course is selected on HomeView, pass the logged-in username and course code to ViewCourseProgressController. The interactor should validate that the course code is present and that the user is enrolled, load the Course through ViewCourseProgressCourseDataAccessInterface, and send the course code, name, target grade, current percentage, estimated grade, completed weight and topic rows to the presenter. The presenter updates the shared CourseProgressViewModel and switches the ViewManagerModel to the course progress view. CourseProgressView should render the state as strings and keep the home navigation wired through the same controller; it should not import entity classes.

Update topic confidence (use case 4, Member B). Add the update_topic_confidence input boundary, input data, interactor, output boundary, output data, controller and presenter. A topic selector on CourseProgressView should send the logged-in username, current course code, topic name and selected confidence name (GREEN, YELLOW or RED) to the controller. The interactor validates the course and topic, converts the string with ConfidenceLevel.fromName(), updates the Topic entity, and saves the course through UpdateTopicConfidenceCourseDataAccessInterface. The presenter should report success or failure as strings and refresh the shared course-progress state so the changed colour is immediately visible. Add getCourse(username, courseCode) and saveCourse(username, course) to the DAO implementation, wire the controller and presenter in AppBuilder, and preserve the current course code and username in the progress view model while the selector is used.

## Testing

Each use case has an interactor unit test written like SignupInteractorTest: build the input data, use InMemoryCourseDataAccessObject and a fake presenter that asserts on the output data, then execute the interactor. Entities (Course, LetterGrade) get plain unit tests.

## Order of work

1. One shared skeleton pull request: entities, CourseFactory, the in-memory DAO, empty views, and HomeView wired into AppBuilder.
2. Each member branches per use case and adds only their own packages, one AppBuilder method, and one interface on the DAO's implements list.
3. Each use case pull request includes its interactor test and is reviewed by another team member before merging.
