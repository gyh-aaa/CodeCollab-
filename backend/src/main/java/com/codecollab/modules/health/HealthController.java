package com.codecollab.modules.health;

import com.codecollab.common.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.time.OffsetDateTime;
import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "系统健康检查", description = "用于确认后端服务是否正常运行")
public class HealthController {

    @GetMapping("/api/health")
    @Operation(summary = "健康检查", description = "返回后端服务状态、服务名和当前时间。")
    public ApiResponse<Map<String, Object>> health() {
        return ApiResponse.ok(Map.of(
            "status", "UP",
            "service", "codecollab-backend",
            "time", OffsetDateTime.now().toString()
        ));
    }
}
