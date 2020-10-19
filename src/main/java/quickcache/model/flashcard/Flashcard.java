package quickcache.model.flashcard;

import java.util.Set;

/**
 * Represents a Flashcard. Each Flashcard will contain one question and
 * one answer. In addition, it can have multiple tags.
 */
public class Flashcard {

    private final Question question;
    private final Set<Tag> tags;
    private final Statistics statistics;

    /**
     * A constructor to create flashcard object.
     *
     * @param question Question of the flashcard.
     * @param tags Tags of the flashcard.
     */
    public Flashcard(Question question, Set<Tag> tags) {
        this.question = question;
        this.tags = tags;
        this.statistics = new Statistics();
    }

    /**
     * A constructor to create flashcard object.
     *
     * @param question Question of the flashcard.
     * @param tags Tags of the flashcard.
     * @param statistics Statistics of the flashcard.
     */
    public Flashcard(Question question, Set<Tag> tags, Statistics statistics) {
        this.question = question;
        this.tags = tags;
        this.statistics = statistics;
    }

    /**
     * Gets the question.
     *
     * @return the question.
     */
    public Question getQuestion() {
        return question;
    }



    /**
     * Checks the given userAnswer with the correct answer.
     *
     * @param userAnswer the user's answer.
     * @return {@code true} if the user's answer is equal to the actual answer.
     */
    public boolean checkAnswer(Answer userAnswer) {
        return question.checkAnswer(userAnswer);
    }

    /**
     * Gets the tags of this flashcard.
     *
     * @return a list of tags.
     */
    public Set<Tag> getTags() {
        return tags;
    }

    public Answer getAnswer() {
        return this.question.getAnswer();
    }

    public Answer getAnswerOrIndex() {
        return this.question.getAnswerOrIndex();
    }

    @Override
    public String toString() {
        return String.format("Question:\n%s\nAnswer:\n%s", question.getFormatQuestion(),
                question.getAnswer().getValue());
    }


    /**
     * A method to check if otherFlashcard is the same with the current one.
     */
    public boolean isSameFlashcard(Flashcard otherFlashcard) {
        if (otherFlashcard == this) {
            return true;
        }

        return otherFlashcard != null
                && otherFlashcard.getQuestion().equals(getQuestion())
                && otherFlashcard.getTags().equals(getTags());
    }

    /**
     * Checks if this flashcard contains the tag.
     *
     * @param tag the tag to be checked.
     * @return {@code true} if this flashcard has the tag.
     */
    public boolean matchTag(Tag tag) {
        return tags.contains(tag);
    }

    /**
     * A method to check if otherFlashcard is the same with the current one.
     */
    @Override
    public boolean equals(Object otherFlashcard) {
        if (otherFlashcard == this) {
            return true;
        } else if (otherFlashcard instanceof Flashcard) {
            Flashcard other = (Flashcard) otherFlashcard;
            return other.getQuestion().equals(getQuestion())
                    && other.getTags().equals(getTags())
                    && other.getStatistics().equals(getStatistics());
        }
        return false;
    }

    public Statistics getStatistics() {
        return statistics;
    }

    public Flashcard getFlashcardAfterClearStatistics() {
        return new Flashcard(question, answer, tags, new Statistics());
    }

    public Flashcard getFlashcardAfterTestSuccess() {
        Statistics newStats = statistics.incrementTimesTested().incrementTimesTestedCorrect();
        return new Flashcard(question, tags, newStats);
    }

    public Flashcard getFlashcardAfterTestFailure() {
        Statistics newStats = statistics.incrementTimesTested();
        return new Flashcard(question, tags, newStats);
    }
}