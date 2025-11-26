//package ru.practicum.stats.collector.utils;
//
//import ru.yandex.practicum.grpc.telemetry.event.*;
//import ru.yandex.practicum.kafka.telemetry.event.*;
//
//import java.util.List;
//import java.util.stream.Collectors;
//
//import static ru.yandex.practicum.grpc.telemetry.event.ScenarioConditionProto.ValueCase.BOOL_VALUE;
//
//public class ScenarioMapper {
//
//    // Маппер для ConditionType (enum)
//    public static ConditionTypeAvro map(ConditionTypeProto type) {
//        if (type == null) {
//            return null;
//        }
//        return ConditionTypeAvro.valueOf(type.name());
//    }
//
//    // Маппер для ConditionOperation (enum)
//    public static ConditionOperationAvro map(ConditionOperationProto operation) {
//        if (operation == null) {
//            return null;
//        }
//
//        return ConditionOperationAvro.valueOf(operation.name());
//    }
//
//    // Маппер для ActionType (enum)
//    public static ActionTypeAvro map(ActionTypeProto type) {
//        if (type == null) {
//            return null;
//        }
//        return ActionTypeAvro.valueOf(type.name());
//    }
//
//    // Маппер для ScenarioCondition
//    public static ScenarioConditionAvro map(ScenarioConditionProto condition) {
//        if (condition == null) {
//            return null;
//        }
//
//        int intValue;
//        boolean boolValue;
//
//        if (condition.getValueCase().equals(BOOL_VALUE)) {
//            boolValue = condition.getBoolValue();
//
//            return ScenarioConditionAvro.newBuilder()
//                    .setSensorId(condition.getSensorId())
//                    .setType(map(condition.getType()))
//                    .setOperation(map(condition.getOperation()))
//                    .setValue(boolValue)
//                    .build();
//        } else {
//
//            intValue = condition.getIntValue();
//
//            return ScenarioConditionAvro.newBuilder()
//                    .setSensorId(condition.getSensorId())
//                    .setType(map(condition.getType()))
//                    .setOperation(map(condition.getOperation()))
//                    .setValue(intValue)
//                    .build();
//        }
//    }
//
//    // Маппер для DeviceAction
//    public static DeviceActionAvro map(DeviceActionProto action) {
//        if (action == null) {
//            return null;
//        }
//        return DeviceActionAvro.newBuilder()
//                .setSensorId(action.getSensorId())
//                .setType(map(action.getType()))
//                .setValue(action.getValue())
//                .build();
//    }
//
//    // Маппер для списка ScenarioCondition
//    public static List<ScenarioConditionAvro> mapConditions(List<ScenarioConditionProto> conditions) {
//        if (conditions == null) {
//            return null;
//        }
//        return conditions.stream()
//                .map(ScenarioMapper::map)
//                .collect(Collectors.toList());
//    }
//
//    // Маппер для списка DeviceAction
//    public static List<DeviceActionAvro> mapActions(List<DeviceActionProto> actions) {
//        if (actions == null) {
//            return null;
//        }
//        return actions.stream()
//                .map(ScenarioMapper::map)
//                .collect(Collectors.toList());
//    }
//}