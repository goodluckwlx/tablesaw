/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package tech.tablesaw.filters;

import tech.tablesaw.api.NumberColumn;
import tech.tablesaw.columns.dates.DateColumnReference;
import tech.tablesaw.columns.dates.PackedLocalDate;
import tech.tablesaw.columns.datetimes.filters.IsInApril;
import org.junit.Before;
import org.junit.Test;
import tech.tablesaw.api.Table;
import tech.tablesaw.columns.datetimes.filters.IsFirstDayOfTheMonth;
import tech.tablesaw.columns.datetimes.filters.IsInFebruary;
import tech.tablesaw.columns.datetimes.filters.IsInMarch;
import tech.tablesaw.api.DateColumn;
import tech.tablesaw.columns.datetimes.filters.IsInYear;
import tech.tablesaw.columns.datetimes.filters.IsLastDayOfTheMonth;
import tech.tablesaw.selection.Selection;

import java.time.LocalDate;
import java.time.Month;

import static org.junit.Assert.*;
import static tech.tablesaw.api.QueryHelper.dateColumn;
import static tech.tablesaw.columns.dates.PackedLocalDate.minusDays;
import static tech.tablesaw.columns.dates.PackedLocalDate.pack;
import static tech.tablesaw.columns.dates.PackedLocalDate.plusDays;


public class DateTableFiltersTest {

    private DateColumn localDateColumn = DateColumn.create("testing");
    private Table table = Table.create("test");
    private DateColumnReference reference = new DateColumnReference("testing");

    @Before
    public void setUp() {
        localDateColumn.append(LocalDate.of(2016, 2, 28)); // sunday
        localDateColumn.append(LocalDate.of(2016, 2, 29)); // monday
        localDateColumn.append(LocalDate.of(2016, 3, 1));  // tues
        localDateColumn.append(LocalDate.of(2016, 3, 2));  // weds
        localDateColumn.append(LocalDate.of(2016, 3, 3));  // thurs
        localDateColumn.append(LocalDate.of(2016, 4, 1));
        localDateColumn.append(LocalDate.of(2016, 4, 2));
        localDateColumn.append(LocalDate.of(2016, 3, 4));   // fri
        localDateColumn.append(LocalDate.of(2016, 3, 5));   // sat
        table.addColumn(localDateColumn);
    }

    @Test
    public void testDow() {
        Selection selection = reference.isSunday().apply(table);
        assertTrue(reference.isSunday().apply(table).contains(0));
        assertTrue(reference.isMonday().apply(table).contains(1));
        assertTrue(reference.isTuesday().apply(table).contains(2));
        assertTrue(reference.isWednesday().apply(table).contains(3));
        assertTrue(reference.isThursday().apply(table).contains(4));
        assertTrue(reference.isFriday().apply(table).contains(7));
        assertTrue(reference.isSaturday().apply(table).contains(8));
        assertFalse(selection.contains(3));
        assertFalse(selection.contains(4));
        assertFalse(selection.contains(5));
        assertFalse(selection.contains(6));
        assertFalse(selection.contains(7));
    }

    @Test
    public void testIsFebruary() {
        IsInFebruary isFebruary = reference.isInFebruary();
        Selection selection = isFebruary.apply(table);
        assertTrue(selection.contains(0));
        assertTrue(selection.contains(1));
        assertFalse(selection.contains(2));
    }

    @Test
    public void testIsMarch() {
        IsInMarch result = reference.isInMarch();
        Selection selection = result.apply(table);
        assertFalse(selection.contains(0));
        assertFalse(selection.contains(1));
        assertTrue(selection.contains(2));
    }

    @Test
    public void testIsApril() {
        IsInApril result = reference.isInApril();
        Selection selection = result.apply(table);
        assertFalse(selection.contains(0));
        assertTrue(selection.contains(5));
        assertTrue(selection.contains(6));
    }

    @Test
    public void testIsFirstDayOfTheMonth() {
        IsFirstDayOfTheMonth result = reference.isFirstDayOfMonth();
        Selection selection = result.apply(table);
        assertFalse(selection.contains(0));
        assertFalse(selection.contains(1));
        assertTrue(selection.contains(2));
        assertTrue(selection.contains(5));
        assertFalse(selection.contains(6));
    }

    @Test
    public void testIsLastDayOfTheMonth() {
        IsLastDayOfTheMonth result = reference.isLastDayOfMonth();
        Selection selection = result.apply(table);
        assertFalse(selection.contains(0));
        assertTrue(selection.contains(1));
        assertFalse(selection.contains(2));
    }

    @Test
    public void testIsInYear() {
        IsInYear result = reference.isInYear(2016);
        Selection selection = result.apply(table);
        assertTrue(selection.contains(0));
        assertTrue(selection.contains(1));
        assertTrue(selection.contains(2));
        result = new IsInYear(reference, 2015);
        selection = result.apply(table);
        assertFalse(selection.contains(0));
        assertFalse(selection.contains(1));
        assertFalse(selection.contains(2));
    }

    @Test
    public void testGetMonthValue() {
        LocalDate date = LocalDate.of(2015, 1, 25);
        Month[] months = Month.values();

        DateColumn dateColumn = DateColumn.create("test");
        for (int i = 0, monthsLength = months.length; i < monthsLength; i++) {
            dateColumn.append(date);
            date = date.plusMonths(1);
        }
        Table t = Table.create("Test");
        t.addColumn(dateColumn);
        NumberColumn index = NumberColumn.indexColumn("index", t.rowCount(), 0);
        t.addColumn(index);

        assertTrue(t.select(dateColumn.isInJanuary()).numberColumn("index").contains(0.0));
        assertTrue(t.select(dateColumn.isInFebruary()).numberColumn("index").contains(1.0));
        assertTrue(t.select(dateColumn.isInMarch()).numberColumn("index").contains(2.0));
        assertTrue(t.select(dateColumn.isInApril()).numberColumn("index").contains(3.0));
        assertTrue(t.select(dateColumn.isInMay()).numberColumn("index").contains(4.0));
        assertTrue(t.select(dateColumn.isInJune()).numberColumn("index").contains(5.0));
        assertTrue(t.select(dateColumn.isInJuly()).numberColumn("index").contains(6.0));
        assertTrue(t.select(dateColumn.isInAugust()).numberColumn("index").contains(7.0));
        assertTrue(t.select(dateColumn.isInSeptember()).numberColumn("index").contains(8.0));
        assertTrue(t.select(dateColumn.isInOctober()).numberColumn("index").contains(9.0));
        assertTrue(t.select(dateColumn.isInNovember()).numberColumn("index").contains(10.0));
        assertTrue(t.select(dateColumn.isInDecember()).numberColumn("index").contains(11.0));

        assertTrue(t.select(dateColumn.isInQ1()).nCol("index").contains(2));
        assertTrue(t.select(dateColumn.isInQ2()).nCol("index").contains(4));
        assertTrue(t.select(dateColumn.isInQ3()).nCol("index").contains(8));
        assertTrue(t.select(dateColumn.isInQ4()).nCol("index").contains(11));
    }

    @Test
    public void testComparison() {
        LocalDate date = LocalDate.of(2015, 1, 25);
        int packed = pack(date);

        DateColumn dateColumn = DateColumn.create("test");

        int before = minusDays(1, packed);
        int equal = packed;
        int after = plusDays(1, packed);

        LocalDate beforeDate = PackedLocalDate.asLocalDate(before);
        LocalDate afterDate = PackedLocalDate.asLocalDate(after);

        dateColumn.appendInternal(before);
        dateColumn.appendInternal(equal);
        dateColumn.appendInternal(after);

        NumberColumn index = NumberColumn.indexColumn("index", dateColumn.size(), 0);
        Table t = Table.create("test", dateColumn, index);

        assertTrue(t.select(dateColumn.isBefore(packed)).nCol("index").contains(0));
        assertTrue(t.select(dateColumn.isEqualTo(packed)).nCol("index").contains(1));
        assertTrue(t.select(dateColumn.isAfter(packed)).nCol("index").contains(2));
        assertTrue(t.select(dateColumn("test")
                .isBetweenExcluding(beforeDate, afterDate)).nCol("index").contains(1));
        assertTrue(t.select(dateColumn("test")
                .isBetweenIncluding(beforeDate, afterDate)).nCol("index").contains(2));
        assertTrue(t.select(dateColumn("test")
                .isBetweenIncluding(beforeDate, afterDate)).nCol("index").contains(0));
        assertFalse(t.select(dateColumn("test")
                .isBetweenExcluding(beforeDate, afterDate)).nCol("index").contains(2));
        assertFalse(t.select(dateColumn("test")
                .isBetweenExcluding(beforeDate, afterDate)).nCol("index").contains(0));
    }

}