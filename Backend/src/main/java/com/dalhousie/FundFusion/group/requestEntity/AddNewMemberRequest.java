package com.dalhousie.FundFusion.group.requestEntity;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AddNewMemberRequest {
    private List<String> newMemberEmails;

}
