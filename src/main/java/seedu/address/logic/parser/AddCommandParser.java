package seedu.address.logic.parser;

import static seedu.address.commons.core.Messages.MESSAGE_INVALID_COMMAND_FORMAT;

import static seedu.address.commons.core.Messages.MESSAGE_START_AFTER_END;
import static seedu.address.logic.parser.CliSyntax.PREFIX_TAG;
import static seedu.address.logic.parser.CliSyntax.PREFIX_STARTDATE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_ENDDATE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_DEADLINE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_ENDDATE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_STARTDATE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_TAG;

import java.util.Date;
import java.util.NoSuchElementException;
import java.util.Set;

import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.logic.commands.AddCommand;
import seedu.address.logic.commands.Command;
import seedu.address.logic.commands.IncorrectCommand;

/**
 * Parses input arguments and creates a new AddCommand object
 */
public class AddCommandParser {

    public static final String EMPTY_STRING = "";

    public static final String NO_START_DATE = "Start date not found";
    public static final String NO_END_DATE = "End date not found";
    
    /**
     * Parses the given {@code String} of arguments in the context of the AddCommand
     * and returns an AddCommand object for execution.
     */
    public Command parse(String args) {
        ArgumentTokenizer argsTokenizer = new ArgumentTokenizer(PREFIX_STARTDATE,PREFIX_ENDDATE,PREFIX_DEADLINE,PREFIX_TAG);

        argsTokenizer.tokenize(args);
        try {
            String name = getNameFromArgsTokenizer(argsTokenizer);
            String startDate = getStartDateFromArgsTokenizer(argsTokenizer);
            String endDate = getEndDateFromArgsTokenizer(argsTokenizer);
            Set<String> tags = getTagsFromArgsTokenizer(argsTokenizer);
            return new AddCommand(name,startDate,endDate,tags);
            
        } catch (NoSuchElementException nsee) {
            return new IncorrectCommand(String.format(MESSAGE_INVALID_COMMAND_FORMAT, AddCommand.MESSAGE_USAGE));
        } catch (IllegalValueException ive) {
            return new IncorrectCommand(ive.getMessage());
        }
    }

    private String getNameFromArgsTokenizer(ArgumentTokenizer argsTokenizer) throws IllegalValueException {
        String name = argsTokenizer.getPreamble().get();
        if (name == EMPTY_STRING) {
            throw new IllegalValueException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, AddCommand.MESSAGE_USAGE));
        }
        
        return name;
    }
    
    private String getStartDateFromArgsTokenizer(ArgumentTokenizer argsTokenizer) throws IllegalValueException{
        if(argsTokenizer.getValue(PREFIX_STARTDATE).isPresent()){
            if(argsTokenizer.getValue(PREFIX_ENDDATE).isPresent()){
                if(isValidStartAndEndDate(argsTokenizer)){
                    return argsTokenizer.getValue(PREFIX_STARTDATE).get();
                
                } else{
                    throw new IllegalValueException(String.format(MESSAGE_START_AFTER_END, AddCommand.MESSAGE_USAGE));
                }
            
            } else {
                throw new IllegalValueException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, AddCommand.MESSAGE_USAGE));
            }
        
        }
        
        return NO_START_DATE;
    }
    
    private boolean isValidStartAndEndDate(ArgumentTokenizer argsTokenizer){
        String startDateString = argsTokenizer.getValue(PREFIX_STARTDATE).get();
        String endDateString = argsTokenizer.getValue(PREFIX_ENDDATE).get();
        
        Date startDate = DateTimeUtil.parseDateTime(startDateString);
        Date endDate = DateTimeUtil.parseDateTime(endDateString);
        
        return startDate.before(endDate);
    }
    
    private String getEndDateFromArgsTokenizer(ArgumentTokenizer argsTokenizer) throws IllegalValueException{
        boolean hasEndDate = argsTokenizer.getValue(PREFIX_ENDDATE).isPresent();
        boolean hasDeadline = argsTokenizer.getValue(PREFIX_DEADLINE).isPresent();
        if(hasDeadline && hasEndDate){
            throw new IllegalValueException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, AddCommand.MESSAGE_USAGE));
        }

        return argsTokenizer
                .getValue(PREFIX_ENDDATE)
                .orElse(
                        argsTokenizer
                        .getValue(PREFIX_DEADLINE)
                        .orElse(NO_END_DATE)
                        );
    }

    private Set<String> getTagsFromArgsTokenizer(ArgumentTokenizer argsTokenizer){
        return ParserUtil.toSet(argsTokenizer.getAllValues(PREFIX_TAG));
    }
}
