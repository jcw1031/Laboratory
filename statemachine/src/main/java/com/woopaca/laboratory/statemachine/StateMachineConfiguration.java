package com.woopaca.laboratory.statemachine;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.statemachine.config.EnableStateMachineFactory;
import org.springframework.statemachine.config.EnumStateMachineConfigurerAdapter;
import org.springframework.statemachine.config.builders.StateMachineConfigurationConfigurer;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;

@Slf4j
@Configuration
@EnableStateMachineFactory(name = "missionStateMachineFactory")
public class StateMachineConfiguration extends EnumStateMachineConfigurerAdapter<StateType, EventType> {

    @Override
    public void configure(StateMachineConfigurationConfigurer<StateType, EventType> config) throws Exception {
        /*config.withConfiguration()
                .autoStartup(true)
                .listener(new StateMachineListenerAdapter<>() {

                    @Override
                    public void stateChanged(State from, State to) {
                        String fromState = (from == null ? "null" : from.getId().name());
                        String toState = to.getId().name();
                        log.info("State changed from {} to {}", fromState, toState);
                    }
                });*/
    }

    @Override
    public void configure(StateMachineStateConfigurer<StateType, EventType> states) throws Exception {
        /*states.withStates()
                .initial(StateType.IDLE)
                .state(StateType.IDLE, stateAction.publishChangedEvent())
                .state(StateType.GOING_TO_LOADING_POINT, stateAction.publishChangedEvent())
                .state(StateType.WAITING_FOR_LOADING, stateAction.publishChangedEvent())
                .state(StateType.LOADING, stateAction.publishChangedEvent())
                .state(StateType.GOING_TO_UNLOADING_POINT, stateAction.publishChangedEvent())
                .state(StateType.WAITING_FOR_UNLOADING, stateAction.publishChangedEvent())
                .state(StateType.UNLOADING, stateAction.publishChangedEvent());*/
    }
}
