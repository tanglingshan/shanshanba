/*
 * Copyright 2024-2026 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.alibaba.cloud.ai.a2a.core.server;

import com.alibaba.cloud.ai.graph.OverAllState;
import com.alibaba.cloud.ai.graph.agent.Agent;
import com.alibaba.cloud.ai.graph.agent.BaseAgent;
import com.alibaba.fastjson.JSON;
import io.a2a.server.agentexecution.RequestContext;
import io.a2a.server.events.EventQueue;
import io.a2a.spec.Event;
import io.a2a.spec.Task;
import io.a2a.spec.TaskState;
import io.a2a.spec.TaskStatusUpdateEvent;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;
import reactor.core.Disposable;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class GraphAgentExecutorTest {

	@Test
	void shouldExtractPreferredOutputForGenericAgent() {
		GraphAgentExecutor executor = new GraphAgentExecutor(mock(Agent.class));
		OverAllState state = new OverAllState(Map.of("answer", "answer-value", "result", "result-value", "output",
				"output-value"));

		String output = extractOutputText(executor, Optional.of(state));

		assertThat(output).isEqualTo("output-value");
	}

	@Test
	void shouldUseConfiguredOutputKeyForBaseAgent() {
		BaseAgent agent = mock(BaseAgent.class);
		when(agent.getOutputKey()).thenReturn("customOutput");
		GraphAgentExecutor executor = new GraphAgentExecutor(agent);
		OverAllState state = new OverAllState(Map.of("output", "fallback", "customOutput", "configured"));

		String output = extractOutputText(executor, Optional.of(state));

		assertThat(output).isEqualTo("configured");
	}

	@Test
	void shouldSerializeStateWhenNoKnownOutputKeyExists() {
		GraphAgentExecutor executor = new GraphAgentExecutor(mock(Agent.class));
		Map<String, Object> data = new LinkedHashMap<>();
		data.put("status", "done");
		data.put("count", 2);

		String output = extractOutputText(executor, Optional.of(new OverAllState(data)));

		assertThat(JSON.parseObject(output)).containsEntry("status", "done").containsEntry("count", 2);
	}

	@Test
	void shouldReturnMessageForMissingOutput() {
		GraphAgentExecutor executor = new GraphAgentExecutor(mock(Agent.class));

		assertThat(extractOutputText(executor, Optional.empty())).isEqualTo("No output in result.");
		assertThat(extractOutputText(executor, Optional.of(new OverAllState()))).isEqualTo("No output in result.");
	}

	@Test
	@SuppressWarnings("unchecked")
	void shouldDisposeActiveStreamAndPublishCanceledStatus() throws Exception {
		GraphAgentExecutor executor = new GraphAgentExecutor(mock(Agent.class));
		Disposable subscription = mock(Disposable.class);
		Map<String, Disposable> activeStreams = (Map<String, Disposable>) ReflectionTestUtils.getField(executor,
				"activeStreams");
		assertThat(activeStreams).isNotNull();
		activeStreams.put("task-1", subscription);

		Task task = mock(Task.class);
		when(task.getId()).thenReturn("task-1");
		RequestContext context = mock(RequestContext.class);
		when(context.getTask()).thenReturn(task);
		when(context.getTaskId()).thenReturn("task-1");
		when(context.getContextId()).thenReturn("context-1");
		EventQueue eventQueue = mock(EventQueue.class);

		executor.cancel(context, eventQueue);

		verify(subscription).dispose();
		assertThat(activeStreams).doesNotContainKey("task-1");
		org.mockito.ArgumentCaptor<Event> eventCaptor = org.mockito.ArgumentCaptor.forClass(Event.class);
		verify(eventQueue).enqueueEvent(eventCaptor.capture());
		assertThat(eventCaptor.getValue()).isInstanceOf(TaskStatusUpdateEvent.class);
		TaskStatusUpdateEvent event = (TaskStatusUpdateEvent) eventCaptor.getValue();
		assertThat(event.getTaskId()).isEqualTo("task-1");
		assertThat(event.getContextId()).isEqualTo("context-1");
		assertThat(event.getStatus().state()).isEqualTo(TaskState.CANCELED);
	}

	private String extractOutputText(GraphAgentExecutor executor, Optional<OverAllState> state) {
		return ReflectionTestUtils.invokeMethod(executor, "extractOutputText", state);
	}

}
