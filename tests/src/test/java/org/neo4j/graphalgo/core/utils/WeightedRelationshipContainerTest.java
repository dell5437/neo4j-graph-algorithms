package org.neo4j.graphalgo.core.utils;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.neo4j.graphalgo.api.RelationshipConsumer;
import org.neo4j.graphalgo.api.RelationshipCursor;
import org.neo4j.graphalgo.api.WeightedRelationshipConsumer;
import org.neo4j.graphalgo.api.WeightedRelationshipCursor;

import java.util.function.Consumer;

import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

/**
 * @author mknblch
 */
@RunWith(MockitoJUnitRunner.class)
public class WeightedRelationshipContainerTest {

    private static WeightedRelationshipContainer container;

    @Mock
    private WeightedRelationshipConsumer consumer;

    @BeforeClass
    public static void setup() {
        container = WeightedRelationshipContainer.builder(2, 10)
                .aim(0, 2)
                    .add(1, 0.1)
                    .add(2, 0.2)
                .aim(1, 1)
                    .add(2, 0.3)
                .build();
    }

    @Test
    public void testV0ForEach() throws Exception {
        container.forEach(0, consumer);
        verify(consumer, times(2)).accept(anyInt(), anyInt(), anyLong(), anyDouble());
        verify(consumer, times(1)).accept(eq(0), eq(1), eq(-1L), eq(0.1));
        verify(consumer, times(1)).accept(eq(0), eq(2), eq(-1L), eq(0.2));
    }

    @Test
    public void testV1ForEach() throws Exception {
        container.forEach(1, consumer);
        verify(consumer, times(1)).accept(anyInt(), anyInt(), anyLong(), anyDouble());
        verify(consumer, times(1)).accept(eq(1), eq(2), eq(-1L), eq(0.3));
    }

    @Test
    public void testVXForEach() throws Exception {
        container.forEach(42, consumer);
        verify(consumer, never()).accept(anyInt(), anyInt(), anyLong(), anyDouble());
    }

    @Test
    public void testV0Iterator() throws Exception {
        container.iterator(0).forEachRemaining(consume(consumer));
        verify(consumer, times(2)).accept(anyInt(), anyInt(), anyLong(), anyDouble());
        verify(consumer, times(1)).accept(eq(0), eq(1), eq(-1L), eq(0.1));
        verify(consumer, times(1)).accept(eq(0), eq(2), eq(-1L), eq(0.2));
    }

    @Test
    public void testV1Iterator() throws Exception {
        container.iterator(1).forEachRemaining(consume(consumer));
        verify(consumer, times(1)).accept(anyInt(), anyInt(), anyLong(), anyDouble());
        verify(consumer, times(1)).accept(eq(1), eq(2), eq(-1L), eq(0.3));
    }

    @Test
    public void testVXIterator() throws Exception {
        container.iterator(42).forEachRemaining(consume(consumer));
        verify(consumer, never()).accept(anyInt(), anyInt(), anyLong(), anyDouble());
    }


    private static Consumer<WeightedRelationshipCursor> consume(WeightedRelationshipConsumer consumer) {
        return cursor -> consumer.accept(cursor.sourceNodeId, cursor.targetNodeId, -1L, cursor.weight);
    }
}
