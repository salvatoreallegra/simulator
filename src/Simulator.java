import java.util.Scanner;
public class Simulator {
    // list of SML instructions
    private static final int READ = 10;
    private static final int WRITE = 11;
    private static final int LOAD = 20;
    private static final int STORE = 21;
    private static final int ADD = 30;
    private static final int SUBTRACT = 31;
    private static final int MULTIPLY = 32;
    private static final int DIVIDE = 33;
    private static final int BRANCH = 40;
    private static final int BRANCH_NEG = 41;
    private static final int BRANCH_ZERO = 42;
    private static final int HALT = 43;

    private static Scanner input = new Scanner(System.in);

    private static int accumulator; // accumulator register
    private static int instructionCounter; // instruction counter, a memory address
    private static int operand; // argument for the operator
    private static int operationCode; // determines the operation
    private static int instructionRegister; // register holding the SML instruction

    private static int[] memory; // simpletron memory
    private static int index = 0; // number of instructions entered in memory

    public static void main(String[] args) {
        // initialize the registers
        initializeRegisters();

        // prompt the user to enter instructions
        printInstructions();
        loadInstructions();

        // execute the program and print the memory dump when finished
        execute();
        dump();
    }

    // set all registers to the correct start value
    public static void initializeRegisters() {
        memory = new int[100];
        accumulator = 0;
        instructionCounter = 0;
        instructionRegister = 0;
        operand = 0;
        operationCode = 0;

        for (int k = 0; k < memory.length; k++) {
            memory[k] = 0;
        }
    }

    // print out user instructions
    public static void printInstructions() {
        System.out.printf("%s\n%s\n%s\n%s\n%s\n%s\n",
                "*** Welcome to Simpletron! ***",
                "*** Please enter your program one instruction   ***",
                "*** (or data word) at a time. I will display    ***",
                "*** the location number and a question mark (?) ***",
                "*** You then type the word for that location.   ***",
                "*** Type -99999 to stop entering your program.  ***");
    }

    // read in user input, test it, perform operations
    public static void loadInstructions() {
        System.out.printf("%02d ? ", index);
        int instruction = input.nextInt();

        while (instruction != -99999 && index < 100) {
            if (validate(instruction)) {
                memory[index++] = instruction;
            }
            else {
                System.out.println("Input invalid.");
            }

            System.out.printf("%02d ? ", index);
            instruction = input.nextInt();
        }

        System.out.println("*** Program loading completed ***");
    }

    // ensure value is within range
    // returns true if the value is within range, otherwise returns false
    public static boolean validate(int value) {
        return (-9999 <= value) && (value <= 9999);
    }

    // ensure that accumulator has not overflowed
    public static boolean testOverflow() {
        if (!validate(accumulator)) {
            System.out.println(
                    "*** Fatal error. Accumulator overflow. ***");
            return true;
        }

        return false;
    }

    // perform all simulator functions
    public static void execute() {
        System.out.println("*** Program execution begins  ***");

        // continue executing until we reach the end of the program
        // it is possible that the program can terminate beforehand though
        while (instructionCounter < index) {
            // read the instruction into the registers
            instructionRegister = memory[instructionCounter];
            operationCode = instructionRegister / 100;
            operand = instructionRegister % 100;

            // go to next instruction, this will only be overridden
            // by the branch commands
            ++instructionCounter;

            switch(operationCode) {
                case READ:
                    // read an integer
                    System.out.print("Enter an integer: ");
                    memory[operand] = input.nextInt();
                    break;

                case WRITE:
                    // outputs the contents of a memory address
                    System.out.printf("Contents of %02d is %d\n",
                            operand, memory[operand]);
                    break;

                case LOAD:
                    // load a memory address into the accumulator
                    accumulator = memory[operand];
                    break;

                case STORE:
                    // store the contents of the accumulator to an address
                    memory[operand] = accumulator;
                    break;

                case ADD:
                    // adds the contents of an address to the accumulator
                    accumulator += memory[operand];

                    if (testOverflow())
                        return;

                    break;

                case SUBTRACT:
                    // subtracts the contents of an address from the
                    // accumulator
                    accumulator -= memory[operand];

                    if (testOverflow()) {
                        return;
                    }

                    break;

                case MULTIPLY:
                    // multiplies the accumulator with the contents of an
                    // address
                    accumulator *= memory[operand];

                    if (testOverflow()) {
                        return;
                    }

                    break;

                case DIVIDE:
                    // divides the accumulator by the contents of an address
                    if (memory[operand] == 0) {
                        System.out.println(
                                "*** Fatal error. Attempt to divide by zero. ***");
                        return;
                    }

                    accumulator /= memory[operand];
                    break;

                case BRANCH:
                    // jumps to an address
                    instructionCounter = operand;
                    break;

                case BRANCH_NEG:
                    // jumps to an address if the accumulator is negative
                    if (accumulator < 0) {
                        instructionCounter = operand;
                    }

                    break;

                case BRANCH_ZERO:
                    // jumps to an address if the accumulator is zero
                    if (accumulator == 0) {
                        instructionCounter = operand;
                    }

                    break;

                case HALT:
                    // terminates execution
                    System.out.println(
                            "*** Simpletron execution terminated ***");
                    return;

                default:
                    // all other cases are not valid opcodes
                    System.out.println(
                            "*** Fatal error. Invalid operation code. ***");
                    return;
            }
        }
    }

    // prints the values of the registers
    public static void displayRegisters() {
        System.out.println("REGISTERS:");
        System.out.printf("%-24s%+05d\n", "Accumulator:", accumulator);
        System.out.printf("%-27s%02d\n", "InstructionCounter:",
                instructionCounter);
        System.out.printf("%-24s%+05d\n", "InstructionRegister:",
                instructionRegister);
        System.out.printf("%-27s%02d\n", "OperationCode:",
                operationCode);
        System.out.printf("%-27s%02d\n", "Operand:", operand);
    }

    // output memory information
    public static void dump() {
        displayRegisters();

        System.out.println("\nMEMORY:");

        // print column headings
        System.out.print("  ");

        for (int k = 0; k < 10; k++) {
            System.out.printf("%7d", k);
        }

        System.out.println();

        // print the memory dump
        for (int k = 0; k < 10; k++) {
            // print the row label
            System.out.printf("%2d", k * 10);

            // print the contents of each memory location
            for (int i = 0; i < 10; i++) {
                System.out.printf("  %+05d", memory[k * 10 + i]);
            }

            System.out.println();
        }
    }
}
