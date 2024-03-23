package seedu.stockpal.parser;

import seedu.stockpal.commands.HelpCommand;
import seedu.stockpal.commands.ListCommand;
import seedu.stockpal.commands.ExitCommand;
import seedu.stockpal.commands.NewCommand;
import seedu.stockpal.commands.EditCommand;
import seedu.stockpal.commands.DeleteCommand;
import seedu.stockpal.commands.InflowCommand;
import seedu.stockpal.commands.OutflowCommand;
import seedu.stockpal.commands.FindCommand;
import seedu.stockpal.commands.Command;

import seedu.stockpal.exceptions.InvalidCommandException;
import seedu.stockpal.exceptions.InvalidFormatException;
import seedu.stockpal.exceptions.UnsignedIntegerExceededException;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static seedu.stockpal.common.Messages.MESSAGE_ERROR_INVALID_COMMAND;
import static seedu.stockpal.common.Messages.MESSAGE_ERROR_INPUT_INTEGER_EXCEEDED;
import static seedu.stockpal.common.Messages.MESSAGE_ERROR_INVALID_FORMAT;

public class Parser {
    public static final String DIVIDER = " ";
    public static final Pattern NEW_COMMAND_PATTERN =
            Pattern.compile("new n/([a-zA-Z0-9 `~!@#$%^&*()\\[\\]{}<>\\-_+=,.?\"':;]{1,50})" +
                    " q/(\\d+)" +
                    "(?: p/(\\d+\\.\\d{2}))?" +
                    "(?: d/([a-zA-Z0-9 `~!@#$%^&*()\\[\\]{}<>\\-_+=,.?\"':;]+))?");
    public static final Pattern EDIT_COMMAND_PATTERN =
            Pattern.compile("edit (\\d+)" +
                    "(?: n/([a-zA-Z0-9 `~!@#$%^&*()\\[\\]{}<>\\-_+=,.?\"':;]{1,50}))?" +
                    "(?: q/(\\d+))?" +
                    "(?: p/(\\d+\\.\\d{2}))?" +
                    "(?: d/([a-zA-Z0-9 `~!@#$%^&*()\\[\\]{}<>\\-_+=,.?\"':;]+))?");
    public static final Pattern DELETE_COMMAND_PATTERN = Pattern.compile("delete (\\d+)");
    public static final Pattern INFLOW_COMMAND_PATTERN = Pattern.compile("inflow (\\d+) a/(\\d+)");
    public static final Pattern OUTFLOW_COMMAND_PATTERN = Pattern.compile("outflow (\\d+) a/(\\d+)");
    public static final Pattern  FIND_COMMAND_PATTERN = Pattern.compile("find ([^\\t\\n\\r\\f]{1,50})");
    public static final int NUM_OF_NEW_COMMAND_ARGUMENTS = 4;
    public static final int NUM_OF_EDIT_COMMAND_ARGUMENTS = 5;
    public static final int NUM_OF_DELETE_COMMAND_ARGUMENTS = 1;
    public static final int NUM_OF_INFLOW_COMMAND_ARGUMENTS = 2;
    public static final int NUM_OF_OUTFLOW_COMMAND_ARGUMENTS = 2;
    public static final int NUM_OF_FIND_COMMAND_ARGUMENTS = 1;
    public static final int START_INDEX = 0;
    private static final Logger LOGGER = Logger.getLogger(Parser.class.getName());

    public Command parseCommand(String input)
            throws InvalidFormatException, InvalidCommandException, UnsignedIntegerExceededException {
        ArrayList<String> parsed;
        input = input.stripLeading();
        String command = getCommandFromInput(input);

        switch (command) {
        case HelpCommand.COMMAND_KEYWORD:
            return createHelpCommand();

        case ListCommand.COMMAND_KEYWORD:
            return createListCommand();

        case ExitCommand.COMMAND_KEYWORD:
            return createExitCommand();

        case NewCommand.COMMAND_KEYWORD:
            parsed = matchAndParseCommand(input, NEW_COMMAND_PATTERN, NUM_OF_NEW_COMMAND_ARGUMENTS);

            assert(parsed.get(0) != null);
            assert(parsed.get(1) != null);
            return validateAndCreateNewCommand(parsed);

        case EditCommand.COMMAND_KEYWORD:
            parsed = matchAndParseCommand(input, EDIT_COMMAND_PATTERN, NUM_OF_EDIT_COMMAND_ARGUMENTS);
            assert(parsed.get(0) != null);
            return validateAndCreateEditCommand(parsed);

        case DeleteCommand.COMMAND_KEYWORD:
            parsed = matchAndParseCommand(input, DELETE_COMMAND_PATTERN, NUM_OF_DELETE_COMMAND_ARGUMENTS);
            assert(parsed.get(0) != null);
            return createDeleteCommand(parsed);

        case InflowCommand.COMMAND_KEYWORD:
            parsed = matchAndParseCommand(input, INFLOW_COMMAND_PATTERN, NUM_OF_INFLOW_COMMAND_ARGUMENTS);
            assert(parsed.get(0) != null);
            assert(parsed.get(1) != null);
            return validateAndCreateInflowCommand(parsed);

        case OutflowCommand.COMMAND_KEYWORD:
            parsed = matchAndParseCommand(input, OUTFLOW_COMMAND_PATTERN, NUM_OF_OUTFLOW_COMMAND_ARGUMENTS);
            assert(parsed.get(0) != null);
            assert(parsed.get(1) != null);
            return validateAndCreateOutflowCommand(parsed);

        case FindCommand.COMMAND_KEYWORD:
            parsed = matchAndParseCommand(input, FIND_COMMAND_PATTERN, NUM_OF_FIND_COMMAND_ARGUMENTS);
            assert(parsed.get(0) != null);
            return validateAndCreateFindCommand(parsed);

        default:
            LOGGER.log(Level.WARNING, MESSAGE_ERROR_INVALID_COMMAND);
            throw new InvalidCommandException(MESSAGE_ERROR_INVALID_COMMAND);
        }
    }

    private ExitCommand createExitCommand() {
        return new ExitCommand();
    }

    private ListCommand createListCommand() {
        return new ListCommand();
    }

    private HelpCommand createHelpCommand() {
        return new HelpCommand();
    }

    private OutflowCommand validateAndCreateOutflowCommand(ArrayList<String> parsed)
            throws UnsignedIntegerExceededException{
        try {
            Integer pid = Integer.parseInt(parsed.get(0));
            Integer decreaseBy = Integer.parseInt(parsed.get(1));
            return new OutflowCommand(pid, decreaseBy);
        } catch (NumberFormatException nfe) {
            LOGGER.log(Level.WARNING, MESSAGE_ERROR_INPUT_INTEGER_EXCEEDED);
            throw new UnsignedIntegerExceededException(MESSAGE_ERROR_INPUT_INTEGER_EXCEEDED);
        }
    }

    private InflowCommand validateAndCreateInflowCommand(ArrayList<String> parsed)
            throws UnsignedIntegerExceededException {
        try {
            Integer pid = Integer.parseInt(parsed.get(0));
            Integer increaseBy = Integer.parseInt(parsed.get(1));
            return new InflowCommand(pid, increaseBy);
        } catch (NumberFormatException nfe) {
            LOGGER.log(Level.WARNING, MESSAGE_ERROR_INPUT_INTEGER_EXCEEDED);
            throw new UnsignedIntegerExceededException(MESSAGE_ERROR_INPUT_INTEGER_EXCEEDED);
        }
    }

    private DeleteCommand createDeleteCommand(ArrayList<String> parsed) throws UnsignedIntegerExceededException {
        try {
            Integer pid = Integer.parseInt(parsed.get(0));
            return new DeleteCommand(pid);
        } catch (NumberFormatException nfe) {
            LOGGER.log(Level.WARNING, MESSAGE_ERROR_INPUT_INTEGER_EXCEEDED);
            throw new UnsignedIntegerExceededException(MESSAGE_ERROR_INPUT_INTEGER_EXCEEDED);
        }
    }

    private EditCommand validateAndCreateEditCommand(ArrayList<String> parsed) throws UnsignedIntegerExceededException{
        Integer pid;
        String name;
        Integer quantity;
        Double price;
        String description;

        name = parsed.get(1);
        try {
            pid = Integer.parseInt(parsed.get(0));
            if (parsed.get(2) != null) {
                quantity = Integer.parseInt(parsed.get(2));
            } else {
                quantity = null;
            }
        } catch (NumberFormatException nfe) {
            LOGGER.log(Level.WARNING, MESSAGE_ERROR_INPUT_INTEGER_EXCEEDED);
            throw new UnsignedIntegerExceededException(MESSAGE_ERROR_INPUT_INTEGER_EXCEEDED);
        }

        if (parsed.get(3) != null) {
            price = Double.parseDouble(parsed.get(3));
        } else {
            price = null;
        }
        description = parsed.get(4);

        return new EditCommand(pid, name, quantity, price, description);

    }

    private NewCommand validateAndCreateNewCommand(ArrayList<String> parsed)
            throws UnsignedIntegerExceededException, InvalidFormatException {
        String name;
        Integer quantity;
        Double price = null;
        String description;

        if (parsed.get(0) == null) {
            throw new InvalidFormatException("Empty name");
        }
        if (parsed.get(1) == null) {
            throw new InvalidFormatException("Empty quantity");
        }

        name = parsed.get(0);
        try {
            quantity = Integer.parseUnsignedInt(parsed.get(1));
        } catch (NumberFormatException nfe) {
            LOGGER.log(Level.WARNING, MESSAGE_ERROR_INPUT_INTEGER_EXCEEDED);
            throw new UnsignedIntegerExceededException(MESSAGE_ERROR_INPUT_INTEGER_EXCEEDED);
        }

        if (parsed.get(2) != null) {
            price = Double.parseDouble(parsed.get(2));
        }
        description = parsed.get(3);
        return new NewCommand(name, quantity, price, description);
    }

    private FindCommand validateAndCreateFindCommand(ArrayList<String> parsed) {
        String name = parsed.get(0);
        return new FindCommand(name);
    }

    private static String getCommandFromInput(String input) {
        if (!input.contains(DIVIDER)) {
            return input;
        }
        return input.substring(START_INDEX, input.indexOf(DIVIDER));
    }

    private static ArrayList<String> matchAndParseCommand(String input, Pattern pattern, int numOfArgs)
            throws InvalidFormatException {
        Matcher matcher = pattern.matcher(input);
        if (!matcher.matches()) {
            LOGGER.log(Level.WARNING, MESSAGE_ERROR_INVALID_FORMAT);
            throw new InvalidFormatException(MESSAGE_ERROR_INVALID_FORMAT);
        }

        ArrayList<String> parsed = new ArrayList<>();
        for (int i = 1; i < numOfArgs + 1; i++) {
            String argument = matcher.group(i);
            if (argument != null) {
                argument = argument.strip();
                if (argument.isEmpty()) {
                    argument = null;
                }
            }
            parsed.add(argument);
        }

        return parsed;
    }
}
