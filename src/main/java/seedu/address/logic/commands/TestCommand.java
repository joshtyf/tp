package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.parser.CliSyntax.PREFIX_ANSWER;
import static seedu.address.logic.parser.CliSyntax.PREFIX_OPTION;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import seedu.address.commons.core.Messages;
import seedu.address.commons.core.index.Index;
import seedu.address.model.flashcard.Answer;
import seedu.address.model.flashcard.Flashcard;
import seedu.address.model.flashcard.MultipleChoiceQuestion;
import seedu.address.model.flashcard.OpenEndedQuestion;
import seedu.address.model.flashcard.Option;
import seedu.address.model.flashcard.Question;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;


public class TestCommand extends Command {

    public static final String COMMAND_WORD = "test";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Tests the specified question "
            + "by the index number used in the displayed question list. "
            + "[" + PREFIX_ANSWER + "ANSWER] "
            + "[" + PREFIX_OPTION + "OPTION] "
            + "Example: " + COMMAND_WORD + " 1 "
            + PREFIX_ANSWER + "Oxygen "
            + PREFIX_OPTION + " 1";


    public static final String MESSAGE_FORMAT = "Expected Answer: %1$s\n"
            + "Your Answer: %2$s";
    public static final String MESSAGE_NO_OPTION_PROVIDED = "An option must be chosen for "
            + "the multiple choice question.";
    public static final String MESSAGE_NO_ANSWER_PROVIDED = "An answer must be chosen for the open ended question.";
    public static final String MESSAGE_NO_OPTION_OR_ANSWER_PROVIDED = "An option or answer must be specified.";
    private final Index index;
    private final TestAnswerDescriptor testAnswerDescriptor;

    /**
     * Instantiates a test command.
     *
     * @param index of the question in the filtered question list to test.
     * @param testAnswerDescriptor details to test the question with.
     */
    public TestCommand(Index index, TestAnswerDescriptor testAnswerDescriptor) {
        requireNonNull(index);
        requireNonNull(testAnswerDescriptor);

        this.index = index;
        this.testAnswerDescriptor = testAnswerDescriptor;
    }

    /**
     * Creates a string output of the test result based on whether it is correct.
     *
     * @param correctAnswer of the question.
     * @param userAnswer that is given.
     * @return string output of the test result.
     */
    private static String getTestResult(Answer correctAnswer, Answer userAnswer) {
        return String.format(MESSAGE_FORMAT, correctAnswer, userAnswer);
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        List<Flashcard> lastShownList = model.getFilteredFlashcardList();

        if (index.getZeroBased() >= lastShownList.size()) {
            throw new CommandException(Messages.MESSAGE_INVALID_FLASHCARD_DISPLAYED_INDEX);
        }

        Flashcard flashcardToTest = lastShownList.get(index.getZeroBased());
        Question question = flashcardToTest.getQuestion();


        if (question instanceof MultipleChoiceQuestion && testAnswerDescriptor.getOption().isEmpty()) {
            throw new CommandException(MESSAGE_NO_OPTION_PROVIDED);
        }
        if (question instanceof OpenEndedQuestion && testAnswerDescriptor.getAnswer().isEmpty()) {
            throw new CommandException(MESSAGE_NO_ANSWER_PROVIDED);
        }

        Answer answer = null;

        if (question instanceof MultipleChoiceQuestion) {
            MultipleChoiceQuestion mcq = (MultipleChoiceQuestion) question;
            Option option = testAnswerDescriptor.getOption().get();
            Index index = option.getIndex();
            answer = mcq.getAnswerFromIndex(index);
        } else if (question instanceof OpenEndedQuestion) {
            OpenEndedQuestion openEndedQuestion = (OpenEndedQuestion) question;
            answer = testAnswerDescriptor.getAnswer().get();
        }

        requireNonNull(answer);
        boolean isCorrect = flashcardToTest.checkAnswer(answer);

        // Initialize an updated flashcard
        Flashcard updatedFlashcard = flashcardToTest;
        if (isCorrect) {
            updatedFlashcard = flashcardToTest.getFlashcardAfterTestSuccess();
        } else {
            updatedFlashcard = flashcardToTest.getFlashcardAfterTestFailure();
        }

        // Updates the flashcardToTest with the new updatedFlashCard (with incremented test count)
        model.setFlashcard(flashcardToTest, updatedFlashcard);
        return new CommandResult(getTestResult(
                flashcardToTest.getAnswer(), answer), question, isCorrect, true);
    }

    @Override
    public boolean equals(Object other) {
        // short circuit if same object
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof TestCommand)) {
            return false;
        }

        // state check
        TestCommand e = (TestCommand) other;
        return index.equals(e.index)
                && testAnswerDescriptor.equals(e.testAnswerDescriptor);
    }

    /**
     * Stores the details to test the question with. Can be used for both open ended and
     * multiple choice questions.
     */
    public static class TestAnswerDescriptor {
        private Answer answer;
        private Option option;

        public TestAnswerDescriptor() {
        }

        public Optional<Answer> getAnswer() {
            return Optional.ofNullable(answer);
        }

        public void setAnswer(Answer answer) {
            this.answer = answer;
        }

        public Optional<Option> getOption() {
            return Optional.ofNullable(option);
        }

        public void setOption(Option option) {
            this.option = option;
        }

        @Override
        public boolean equals(Object other) {
            if (this == other) {
                return true;
            }
            if (other == null || getClass() != other.getClass()) {
                return false;
            }
            TestAnswerDescriptor that = (TestAnswerDescriptor) other;
            return Objects.equals(answer, that.answer)
                    && Objects.equals(option, that.option);
        }

        @Override
        public int hashCode() {
            return Objects.hash(answer, option);
        }

        public boolean isAnyFieldPresent() {
            return answer != null || option != null;
        }
    }
}
