# University Learning Progress Tracker

A Java Swing desktop application that lets university students track how they are doing in each course they are enrolled in.

## Program overview

A student signs up, logs in, and lands on a home page listing their enrolled courses. From there they can:

- **Enrol in a course** by entering its code, name, target grade and main topics/chapters.
- **Open a course's progress page**, showing the estimated and target letter grades (A, B, C, ...) and each topic marked green (confident), yellow (not so confident) or red (needs more attention).
- **Calculate their current grade** in a pop-up, by entering each test/assignment/exam mark and its weight.
- **Set a target grade** and see the average they need on the rest of the course.
- **Rank their subjects by confidence**: courses are grouped by subject (all MAT courses, all CSC courses, ...) and ranked by the average confidence of their topics.

Data is saved per user, so courses, topic confidence and marks are still there after a restart.

## Use cases

Signup, login, logout and change password are reused from the starter code. Each new use case is one CA engine.

| # | Use case | Trigger | What the interactor does | Owner | Status |
|---|---|---|---|---|---|
| 1 | Enrol in course | Enrol button on home | Validates code, name, target grade and topics; rejects duplicates; saves a new `Course` | Member A | Done (PR #3) |
| 2 | View enrolled courses | Home page after login | Loads the user's courses with estimated vs target grade for the home list | Member A | Done |
| 3 | View course progress | Click a course on home | Loads one course's grades and topics and switches to the progress page | Member B | Done (PR #5) |
| 4 | Update topic confidence | Colour selector on a topic | Sets the topic's confidence level and saves | Member B | Done |
| 5 | Calculate current grade | Calculate in the pop-up | Validates marks and weights, computes the weighted grade, saves the assessments, updates the estimated grade | Member C | Done (PR #4) |
| 6 | Set target grade | Target selector on progress page | Updates the target grade and reports the average needed on the remaining weight | Member C | Done |
| 7 | Rank subjects by confidence | Subject Ranking button on home | Groups courses by subject code (the letters of the course code, e.g. MAT), averages every topic's confidence (green 100%, yellow 50%, red 0%) and ranks subjects from most to least confident; ties share a rank | Member C | Done |

Each member owns one screen area (home, progress page, pop-ups) so pull requests do not collide. `Main`, `AppBuilder`, the entities, factories and data access objects are shared. Possible extensions: Add Topic, Drop Course.

## User stories

**Use cases 3 and 4: View and update course progress.** As a user, I can open one of my courses and update my confidence in its topics, so that I can see what I understand and which topics need more attention.
> Mia selects CSC207 on the home page. The progress page shows her estimated grade, target grade and every topic with its confidence colour. She now feels confident with Clean Architecture, so she changes it from yellow to green; the change is saved and the page shows it straight away.

**Use cases 5 and 6: Calculate current grade and set a target.** As a user, I can calculate my current grade from my marks and set a target grade, so that I know how I am doing in a course and what average I need on the rest of it.
> Mia opens the grade calculator for CSC207 and enters Midterm 1 (78%, worth 15%) and Assignment 1 (92%, worth 10%). The calculator shows a current grade of 83.6% (A-) based on 25% of the course, saves the marks, and her progress page now shows an estimated grade of A-. She then raises her target from A- to A, and the page tells her she needs an average of 85.5% on the remaining 75%, so she plans extra practice before the final exam.

**Use case 7: Rank subjects by confidence.** As a user, I can know which category of courses I am good at, so that I know where to spend my study time.
> Mia is enrolled in CSC207, CSC148, MAT137 and STA247. She presses Subject Ranking on the home page and sees Mathematics first at 83%, Computer Science second at 30% and Statistics last at 0%, so she spends the weekend on Statistics.

## Architecture

Every use case follows the same flow as the starter's login example. Dependencies point inward: entities import nothing from other layers, use cases import only entities, interface adapters import only use cases, and presenters hand views plain strings so views never import entities.

```
View -> Controller -> InputData -> InputBoundary (Interactor)
                                        |  uses Entities and the DataAccessInterface
                                        v
View <- ViewModel <- Presenter <- OutputBoundary <- OutputData
```

**Design pattern beyond the starter code: Strategy.** How topic confidence is scored for the subject ranking (use case 7) is a `ConfidenceScoringStrategy` in the entity layer. `StandardConfidenceScoring` counts green as 1, yellow as 0.5 and red as 0, and decides which colour an average counts as. `AppBuilder` injects it into `RankSubjectsInteractor`, so a different scoring rule is a new class rather than a change to the interactor (Open/Closed, Dependency Inversion); `RankSubjectsInteractorTest` ranks the same courses with two strategies to show this.

## Package structure

```
src/main/java
├── app/               Main (chain grows per use case), AppBuilder (one addXView / addXUseCase each)
├── entity/            User, UserFactory (reused); Course, CourseFactory, Topic, Assessment;
│                      ConfidenceLevel (GREEN/YELLOW/RED, fromName());
│                      LetterGrade (A+..F, fromPercentage(), fromLabel());
│                      Subject (a subject code and its courses; average confidence);
│                      ConfidenceScoringStrategy + StandardConfidenceScoring (Strategy)
├── use_case/          signup/ login/ logout/ change_password/ (existing)
│                      enrol_course/ view_courses/ view_course_progress/
│                      update_topic_confidence/ calculate_grade/ set_target_grade/ rank_subjects/
│                      each: XInputBoundary, XInputData, XInteractor, XOutputBoundary,
│                            XOutputData, XCourseDataAccessInterface
├── interface_adapter/ ViewManagerModel (existing)
│   ├── home/                     HomeViewModel, HomeState (replaces logged_in)
│   ├── enrol_course/             Controller, Presenter, EnrolViewModel, EnrolState
│   ├── view_courses/             Controller, Presenter
│   ├── course_progress/          CourseProgressViewModel, CourseProgressState, TopicRow,
│   │                             AssessmentRow, TargetMessageFormatter,
│   │                             ViewCourseProgress Controller + Presenter
│   ├── update_topic_confidence/  Controller, Presenter
│   ├── calculate_grade/          Controller, Presenter, GradeCalculatorViewModel, State
│   ├── set_target_grade/         Controller, Presenter
│   └── rank_subjects/            Controller, Presenter, SubjectRankingViewModel, State
├── data_access/       FileUserDataAccessObject (existing);
│                      DBCourseDataAccessObject (API-backed; implements every course interface);
│                      InMemoryCourseDataAccessObject (tests, and until the DB DAO exists)
└── view/              SignupView, LoginView, ViewManager, LabelTextPanel (existing);
                       HomeView (course list, Enrol and Subject Ranking buttons; from LoggedInView);
                       EnrolView; CourseProgressView (grades, topic colours, target selector,
                       Calculate Grade button); GradeCalculatorDialog; SubjectRankingDialog

src/test/java/use_case/<each use case>/XInteractorTest.java
```

## Data persistence and external API

Planned: the course-provided API used by `DBUserDataAccessObject` on the starter repo's `db-user` branch, storing each user's courses as one JSON object. This covers both the external API and persistence requirements. To confirm first: the API's endpoints and whether it can store nested course/topic/assessment data.

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

## Integration notes

**Use case 2 (done).** `LoggedInView` (the home page until `HomeView` replaces it) reloads its course list every time it is shown, i.e. after logging in, after enrolling and after returning from a progress page, by calling `ViewCoursesController` with the logged-in username. The interactor loads the user's courses through `ViewCoursesCourseDataAccessInterface` (`getCourses`) and sends each course's code, name, target grade, current percentage, estimated grade and whether the target is reached. `ViewCoursesPresenter` turns these into `CourseRow`s (for example `CSC207 - Software Design | Estimated: A- (83.6%) | Target: A | Below target`) on `LoggedInState`, plus a message such as "3 enrolled courses" or a hint to enrol when the list is empty. Selecting a course and pressing View Progress, or double-clicking it, runs use case 3. Wired with `addViewCoursesUseCase`.

**Use case 3 (done).** Selecting a course on home passes the username and course code to `ViewCourseProgressController`. The interactor checks the course code is present and the user is enrolled, loads the course through `ViewCourseProgressCourseDataAccessInterface`, and sends the code, name, target grade, current percentage, estimated grade, completed weight and topic rows to the presenter, which updates the shared `CourseProgressViewModel` and switches to the progress view. `CourseProgressView` renders strings only and uses the same controller for home navigation.

**Use case 4 (done).** The topic selectors on `CourseProgressView` send the username, course code, topic name and confidence name (GREEN, YELLOW or RED) to the controller. The interactor validates the course and topic, converts the name with `ConfidenceLevel.fromName()`, updates the `Topic` and saves through `UpdateTopicConfidenceCourseDataAccessInterface` (`getCourse`, `saveCourse`). The presenter refreshes the shared progress state so the new colour and description show at once, and the use case is wired in `AppBuilder`.

**Use cases 5, 6 and 7.** Wired with `addCalculateGradeUseCase`, `addSetTargetGradeUseCase` and `addRankSubjectsUseCase`. Run `src/test/java/app/GradeToolsDemo.java` from the IDE to try them without the full app. To connect them:
- **Progress page (Member B):** when a course is opened, also fill the state's `username` and `assessments` (the calculator starts from these rows). The `Calculate Grade` button on `CourseProgressView` calls `appBuilder.getGradeCalculatorDialog().open()`, and the target-grade panel comes from `appBuilder.getTargetGradePanel()`.
- **Home page (Member A):** add a Subject Ranking button that calls `appBuilder.getSubjectRankingDialog().open(username)`.
- **Course DAO:** `AppBuilder` uses `InMemoryCourseDataAccessObject` for now, so courses are lost on restart. `DBCourseDataAccessObject` must implement `CalculateGradeCourseDataAccessInterface` and `SetTargetGradeCourseDataAccessInterface` (`getCourse`, `saveCourse`) and `RankSubjectsCourseDataAccessInterface` (`getCourses`); then switch the field marked TODO in `AppBuilder`.
- **Letter grades:** the target selector reads `CourseProgressViewModel.GRADE_OPTIONS`, a copy of the `LetterGrade` labels so views do not import entities; `TargetMessageFormatterTest` fails if the two drift apart.

## Testing

Each use case has an interactor test written like `SignupInteractorTest`: build the input data, use `InMemoryCourseDataAccessObject` and a fake presenter that asserts on the output data, then execute the interactor. Entities (`Course`, `LetterGrade`, `Subject`) have plain unit tests, and presenters that format text have their own tests.

## Order of work

1. One shared skeleton pull request: entities, `CourseFactory`, the in-memory DAO, empty views, and `HomeView` wired into `AppBuilder`.
2. Each member branches per use case and adds only their own packages, one `AppBuilder` method, and one interface on the DAO's `implements` list.
3. Each use case pull request includes its interactor test and is reviewed by another team member before merging.
