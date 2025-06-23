package org.opendatamesh.platform.pp.marketplace.rest.v1.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.opendatamesh.platform.pp.marketplace.accessrequests.services.core.ExecutorResponsesService;
import org.opendatamesh.platform.pp.marketplace.rest.v1.resources.executors.ExecutorResponseSearchOptions;
import org.opendatamesh.platform.pp.marketplace.rest.v1.resources.executors.MarketplaceExecutorResponseRes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/api/v1/pp/marketplace/executor-responses")
@Tag(name = "Executor Responses", description = "Endpoints for managing executor responses")
public class ExecutorResponsesController {

    @Autowired
    private ExecutorResponsesService executorResponsesService;

    @Operation(summary = "Get executor response by ID", description = "Retrieves a specific executor response by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Executor response found",
                    content = @Content(schema = @Schema(implementation = MarketplaceExecutorResponseRes.class))),
            @ApiResponse(responseCode = "404", description = "Executor response not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public MarketplaceExecutorResponseRes getExecutorResponse(
            @Parameter(description = "Executor response ID", required = true)
            @PathVariable("id") Long id
    ) {
        return executorResponsesService.findOneResource(id);
    }

    @Operation(summary = "Search executor responses", description = "Retrieves a paginated list of executor responses based on search criteria. " +
            "The results can be sorted by any of the following properties: id, accessRequestIdentifier, status, message, " +
            "providerDataProductFqn, createdAt, updatedAt. " +
            "Sort direction can be specified using 'asc' or 'desc' (e.g., 'sort=status,desc'). " +
            "Filter by accessRequestIdentifier or accessRequestUuid using query parameters.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Executor responses found",
                    content = @Content(schema = @Schema(implementation = Page.class))),
            @ApiResponse(responseCode = "400", description = "Invalid search parameters or invalid sort property"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Page<MarketplaceExecutorResponseRes> searchExecutorResponses(
            @Parameter(description = "Search options for filtering executor responses")
            ExecutorResponseSearchOptions searchOptions,
            @Parameter(description = "Pagination and sorting parameters. Default sort is by createdAt in descending order. " +
                    "Valid sort properties are: id, accessRequestIdentifier, status, message, providerDataProductFqn, createdAt, updatedAt")
            @PageableDefault(page = 0, size = 20, sort = "createdAt", direction = Sort.Direction.DESC)
            Pageable pageable
    ) {
        return executorResponsesService.findAllResourcesFiltered(pageable, searchOptions);
    }
} 