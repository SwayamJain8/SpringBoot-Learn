package net.engineeringdigest.journalApp.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import net.engineeringdigest.journalApp.dto.JournalEntryDTO;
import net.engineeringdigest.journalApp.entity.JournalEntry;
import net.engineeringdigest.journalApp.entity.User;
import net.engineeringdigest.journalApp.service.JournalEntryService;
import net.engineeringdigest.journalApp.service.UserService;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/journal")
@Tag(name = "Journal APIs" , description = "Create, Read, Update & Delete Journal Entries")
public class JournalEntryController {

    @Autowired
    private JournalEntryService journalEntryService;

    @Autowired
    private UserService userService;

    @GetMapping
    @Operation(summary = "Get all journal entries of a user")
    public ResponseEntity<?> getAllJournalEntriesOfUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userName = authentication.getName();
        User user = userService.findByUserName(userName);
        List<JournalEntry> all = user.getJournalEntries();
        if(all != null && !all.isEmpty()) {
            return new ResponseEntity<>(all, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping
    @Operation(summary = "Create a new journal entry")
    public ResponseEntity<JournalEntry> createEntry(@RequestBody JournalEntryDTO myEntry) {
        try {
            JournalEntry journalEntry = new JournalEntry();
            journalEntry.setTitle(myEntry.getTitle());
            journalEntry.setContent(myEntry.getContent());
            journalEntry.setSentiment(myEntry.getSentiment());
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String userName = authentication.getName();
            journalEntryService.saveEntry(journalEntry, userName);
            return new ResponseEntity<>(journalEntry, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("id/{myId}")
    @Operation(summary = "Get a journal entry by id")
    public ResponseEntity<?> getJournalEntryById(@PathVariable String myId) {
        ObjectId objectId = new ObjectId(myId);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userName = authentication.getName();
        User user = userService.findByUserName(userName);
        List<JournalEntry> collect = user.getJournalEntries().stream().filter(x -> x.getId().equals(objectId)).collect(Collectors.toList());
        if(!collect.isEmpty()) {
            Optional<JournalEntry> journalEntry = journalEntryService.findById(objectId);
            if(journalEntry.isPresent()) {
                return new ResponseEntity<>(journalEntry.get(), HttpStatus.OK);
            }
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @DeleteMapping("id/{myId}")
    @Operation(summary = "Delete a journal entry by id")
    public ResponseEntity<?> deleteJournalEntryById(@PathVariable String myId) {
        ObjectId objectId = new ObjectId(myId);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userName = authentication.getName();
        boolean removed = journalEntryService.deleteById(objectId, userName);
        if(removed) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            return  new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("id/{myId}")
    @Operation(summary = "Update a journal entry by id")
    public ResponseEntity<?> updateJournalEntry(
                @PathVariable String myId,
                @RequestBody JournalEntryDTO newEntry
            ) {
                ObjectId objectId = new ObjectId(myId);
                JournalEntry newjournalEntry = new JournalEntry();
                newjournalEntry.setTitle(newEntry.getTitle());
                newjournalEntry.setContent(newEntry.getContent());
                newjournalEntry.setSentiment(newEntry.getSentiment());
                Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
                String userName = authentication.getName();
                User user = userService.findByUserName(userName);
                List<JournalEntry> collect = user.getJournalEntries().stream().filter(x -> x.getId().equals(objectId)).collect(Collectors.toList());
                if(!collect.isEmpty()) {
                    Optional<JournalEntry> journalEntry = journalEntryService.findById(objectId);
                    if(journalEntry.isPresent()) {
                        JournalEntry old = journalEntry.get();
                        old.setTitle(newjournalEntry.getTitle() != null && !newjournalEntry.getTitle().equals("") ? newjournalEntry.getTitle() : old.getTitle());
                        old.setContent(newjournalEntry.getContent() != null && !newjournalEntry.getContent().equals("") ? newjournalEntry.getContent() : old.getContent());
                        old.setSentiment(newjournalEntry.getSentiment() != null ? newjournalEntry.getSentiment() : old.getSentiment());
                        journalEntryService.saveEntry(old);
                        return new ResponseEntity<>(old, HttpStatus.OK);
                    }
                }
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

}
