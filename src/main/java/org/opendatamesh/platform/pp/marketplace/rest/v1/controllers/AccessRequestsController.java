package org.opendatamesh.platform.pp.marketplace.rest.v1.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.opendatamesh.platform.pp.marketplace.accessrequests.services.AccessRequestsUtilsService;
import org.opendatamesh.platform.pp.marketplace.accessrequests.services.core.AccessRequestsService;
import org.opendatamesh.platform.pp.marketplace.rest.v1.resources.accessrequests.AccessRequestRes;
import org.opendatamesh.platform.pp.marketplace.rest.v1.resources.accessrequests.AccessRequestSearchOptions;
import org.opendatamesh.platform.pp.marketplace.rest.v1.resources.executors.MarketplaceExecutorResponseRes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/api/v1/pp/marketplace/requests")
@Tag(name = "Access Requests", description = "Endpoints for managing access requests")
public class AccessRequestsController {

    @Autowired
    private AccessRequestsUtilsService utilsService;

    @Autowired
    private AccessRequestsService accessRequestsService;

    @Operation(summary = "Submit a new access request", description = "Creates a new access request in the marketplace")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Access request created successfully",
                    content = @Content(schema = @Schema(implementation = AccessRequestRes.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request parameters"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping("/submit")
    @ResponseStatus(HttpStatus.CREATED)
    public AccessRequestRes submitAccessRequest(
            @Parameter(description = "Access request details", required = true)
            @RequestBody AccessRequestRes accessRequest
    ) {
        return utilsService.submitAccessRequest(accessRequest);
    }

    @Operation(summary = "Get access request by ID", description = "Retrieves a specific access request by its UUID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Access request found",
                    content = @Content(schema = @Schema(implementation = AccessRequestRes.class))),
            @ApiResponse(responseCode = "404", description = "Access request not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public AccessRequestRes getAccessRequest(
            @Parameter(description = "Access request UUID", required = true)
            @PathVariable("id") String id
    ) {
        return accessRequestsService.findOneResource(id);
    }

    @Operation(summary = "Search access requests", description = "Retrieves a paginated list of access requests based on search criteria. " +
            "The results can be sorted by any of the following properties: uuid, name, identifier, operation, reviewerIdentifier, " +
            "consumerType, consumerIdentifier, providerDataProductFqn, properties, startDate, endDate, createdAt, updatedAt. " +
            "Sort direction can be specified using 'asc' or 'desc' (e.g., 'sort=name,desc').")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Access requests found",
                    content = @Content(schema = @Schema(implementation = Page.class))),
            @ApiResponse(responseCode = "400", description = "Invalid search parameters or invalid sort property"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Page<AccessRequestRes> searchAccessRequests(
            @Parameter(description = "Search options for filtering access requests")
            AccessRequestSearchOptions searchOptions,
            @Parameter(description = "Pagination and sorting parameters. Default sort is by createdAt in descending order. " +
                    "Valid sort properties are: uuid, name, identifier, operation, reviewerIdentifier, consumerType, " +
                    "consumerIdentifier, providerDataProductFqn, properties, startDate, endDate, createdAt, updatedAt")
            @PageableDefault(page = 0, size = 20, sort = "createdAt", direction = Sort.Direction.DESC)
            Pageable pageable
    ) {
        return accessRequestsService.findAllResourcesFiltered(pageable, searchOptions);
    }

    @Operation(summary = "Delete access request", description = "Deletes an access request by its UUID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Access request deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Access request not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteAccessRequest(
            @Parameter(description = "Access request UUID", required = true)
            @PathVariable("id") String id
    ) {
        accessRequestsService.delete(id);
    }

    @Operation(summary = "Handle executor response", description = "Processes the response from the marketplace executor for a specific access request")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Response processed successfully"),
            @ApiResponse(responseCode = "404", description = "Access request not found"),
            @ApiResponse(responseCode = "400", description = "Invalid response data"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping("/{identifier}/results")
    @ResponseStatus(HttpStatus.OK)
    public void handleExecutorResponse(
            @Parameter(description = "The Access Request uuid", required = true)
            @PathVariable("identifier") String identifier,
            @Parameter(description = "Executor response details", required = true)
            @RequestBody MarketplaceExecutorResponseRes response
    ) {
        utilsService.uploadAccessRequestResult(identifier, response);
    }
}
