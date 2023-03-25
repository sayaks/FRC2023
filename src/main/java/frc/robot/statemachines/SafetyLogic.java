package frc.robot.statemachines;

import edu.wpi.first.math.controller.PIDController;

/**
 * A SafetyLogic is used to ensure that mechanisms dont move beyond the bounds
 * that it is safe for them to
 * move within.
 *
 * An object of this type can exist in one of five states of
 * {@link SafetyLogic.State State}, representing the
 * target state of that logic.
 *
 * The Data generic is used for any extra data that the logic requires.
 */
public abstract class SafetyLogic<Data> {
    /**
     * The main PID controller used by this logic in
     * {@link SafetyLogic#stateCalculate stateCalculate}.
     */
    protected PIDController mainPid;
    /**
     * The target position of the current state.
     */
    protected final double position;
    /**
     * The target state.
     */
    protected final State state;
    /**
     * Extra data used by this logic.
     */
    protected final Data data;

    /**
     * Construct a new logic in the given state.
     *
     * Sets {@link SafetyLogic#position position} and {@link SafetyLogic#data data}
     * according to the output of {@link SafetyConstants#getPosition} and
     * {@link SafetyConstants#getdata} respectively.
     *
     * @param state Which state to create the logic in.
     */
    public SafetyLogic(State state) {
        this.state = state;
        SafetyConstants<Data> constants = get_constants();
        this.position = constants.getPosition(state);
        this.data = constants.getData(state);
    }

    /**
     * @return The constants used to initialize a safetylogic in a give state.
     */
    protected abstract SafetyConstants<Data> get_constants();

    /**
     * Calculate a new speed for this subsystem given the speed and positions of the
     * other subsystems. Such that all safety invariants are upheld.
     *
     * @param speed            The speed to move at
     * @param elbowPosition    The current position of the elbow
     * @param wristPosition    The current position of the wrist
     * @param elevatorPosition The current position of the elevator
     * @param carriagePosition The current position of the carriage
     * @return The new speed for this subsystem
     */
    public abstract double stateCalculate(double speed, double elbowPosition, double wristPosition,
            double elevatorPosition, double carriagePosition);

    /**
     * A state that a subsystem can be in.
     */
    public static enum State {
        start, low, mid, loading, high
    }

    /**
     * Constants specifying the safety invariants.
     */
    public static interface SafetyConstants<Data> {
        /**
         * @param state Which state to get the target position of
         * @return The target position
         */
        public abstract double getPosition(State state);

        /**
         * @param state Which state to get the data for
         * @return The extra data used by a safety logic for safety invariants.
         */
        public abstract Data getData(State state);
    }
}
