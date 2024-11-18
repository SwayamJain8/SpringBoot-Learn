package net.engineeringdigest.journalApp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.engineeringdigest.journalApp.enums.Sentiment;

import javax.validation.constraints.NotEmpty;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class JournalEntryDTO {
    @NotEmpty
    private String title;
    private String content;
    private Sentiment sentiment;
}
