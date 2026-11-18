package interface_adapter.set_target_grade;

import use_case.set_target_grade.SetTargetGradeInputBoundary;
import use_case.set_target_grade.SetTargetGradeInputData;

/**
 * The controller for the Set Target Grade Use Case.
 */
public class SetTargetGradeController {

    private final SetTargetGradeInputBoundary setTargetGradeUseCaseInteractor;

    public SetTargetGradeController(SetTargetGradeInputBoundary setTargetGradeUseCaseInteractor) {
        this.setTargetGradeUseCaseInteractor = setTargetGradeUseCaseInteractor;
    }

    /**
     * Executes the Set Target Grade Use Case.
     * @param username the user whose course is being changed
     * @param courseCode the code of the course
     * @param targetGrade the label of the new target grade, such as "A-"
     */
    public void execute(String username, String courseCode, String targetGrade) {
        final SetTargetGradeInputData inputData = new SetTargetGradeInputData(
                username, courseCode, targetGrade);
        setTargetGradeUseCaseInteractor.execute(inputData);
    }
}
