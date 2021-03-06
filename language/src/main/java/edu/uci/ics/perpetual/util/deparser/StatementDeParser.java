
package edu.uci.ics.perpetual.util.deparser;

import java.util.Iterator;
import edu.uci.ics.perpetual.statement.Block;
import edu.uci.ics.perpetual.statement.Commit;
import edu.uci.ics.perpetual.statement.SetStatement;
import edu.uci.ics.perpetual.statement.Statement;
import edu.uci.ics.perpetual.statement.StatementVisitor;
import edu.uci.ics.perpetual.statement.Statements;
import edu.uci.ics.perpetual.statement.UseStatement;
import edu.uci.ics.perpetual.statement.alter.Alter;
import edu.uci.ics.perpetual.statement.comment.Comment;
import edu.uci.ics.perpetual.statement.create.index.CreateIndex;
import edu.uci.ics.perpetual.statement.create.table.CreateTable;
import edu.uci.ics.perpetual.statement.create.view.AlterView;
import edu.uci.ics.perpetual.statement.create.view.CreateView;
import edu.uci.ics.perpetual.statement.delete.Delete;
import edu.uci.ics.perpetual.statement.drop.Drop;
import edu.uci.ics.perpetual.statement.execute.Execute;
import edu.uci.ics.perpetual.statement.insert.Insert;
import edu.uci.ics.perpetual.statement.merge.Merge;
import edu.uci.ics.perpetual.statement.replace.Replace;
import edu.uci.ics.perpetual.statement.select.Select;
import edu.uci.ics.perpetual.statement.select.WithItem;
import edu.uci.ics.perpetual.statement.truncate.Truncate;
import edu.uci.ics.perpetual.statement.update.Update;
import edu.uci.ics.perpetual.statement.upsert.Upsert;
import edu.uci.ics.perpetual.statement.values.ValuesStatement;

public class StatementDeParser implements StatementVisitor {

    private ExpressionDeParser expressionDeParser;

    private SelectDeParser selectDeParser;

    protected StringBuilder buffer;

    public StatementDeParser(StringBuilder buffer) {
        this(new ExpressionDeParser(), new SelectDeParser(), buffer);
    }

    public StatementDeParser(ExpressionDeParser expressionDeParser, SelectDeParser selectDeParser, StringBuilder buffer) {
        this.expressionDeParser = expressionDeParser;
        this.selectDeParser = selectDeParser;
        this.buffer = buffer;
    }

    @Override
    public void visit(CreateIndex createIndex) {
        CreateIndexDeParser createIndexDeParser = new CreateIndexDeParser(buffer);
        createIndexDeParser.deParse(createIndex);
    }

    @Override
    public void visit(CreateTable createTable) {
        CreateTableDeParser createTableDeParser = new CreateTableDeParser(this, buffer);
        createTableDeParser.deParse(createTable);
    }

    @Override
    public void visit(CreateView createView) {
        CreateViewDeParser createViewDeParser = new CreateViewDeParser(buffer);
        createViewDeParser.deParse(createView);
    }

    @Override
    public void visit(AlterView alterView) {
        AlterViewDeParser alterViewDeParser = new AlterViewDeParser(buffer);
        alterViewDeParser.deParse(alterView);
    }

    @Override
    public void visit(Delete delete) {
        selectDeParser.setBuffer(buffer);
        expressionDeParser.setSelectVisitor(selectDeParser);
        expressionDeParser.setBuffer(buffer);
        selectDeParser.setExpressionVisitor(expressionDeParser);
        DeleteDeParser deleteDeParser = new DeleteDeParser(expressionDeParser, buffer);
        deleteDeParser.deParse(delete);
    }

    @Override
    public void visit(Drop drop) {
        DropDeParser dropDeParser = new DropDeParser(buffer);
        dropDeParser.deParse(drop);
    }

    @Override
    public void visit(Insert insert) {
        selectDeParser.setBuffer(buffer);
        expressionDeParser.setSelectVisitor(selectDeParser);
        expressionDeParser.setBuffer(buffer);
        selectDeParser.setExpressionVisitor(expressionDeParser);
        InsertDeParser insertDeParser = new InsertDeParser(expressionDeParser, selectDeParser, buffer);
        insertDeParser.deParse(insert);
    }

    @Override
    public void visit(Replace replace) {
        selectDeParser.setBuffer(buffer);
        expressionDeParser.setSelectVisitor(selectDeParser);
        expressionDeParser.setBuffer(buffer);
        selectDeParser.setExpressionVisitor(expressionDeParser);
        ReplaceDeParser replaceDeParser = new ReplaceDeParser(expressionDeParser, selectDeParser, buffer);
        replaceDeParser.deParse(replace);
    }

    @Override
    public void visit(Select select) {
        selectDeParser.setBuffer(buffer);
        expressionDeParser.setSelectVisitor(selectDeParser);
        expressionDeParser.setBuffer(buffer);
        selectDeParser.setExpressionVisitor(expressionDeParser);
        if (select.getWithItemsList() != null && !select.getWithItemsList().isEmpty()) {
            buffer.append("WITH ");
            for (Iterator<WithItem> iter = select.getWithItemsList().iterator(); iter.hasNext();) {
                WithItem withItem = iter.next();
                withItem.accept(selectDeParser);
                if (iter.hasNext()) {
                    buffer.append(",");
                }
                buffer.append(" ");
            }
        }
        select.getSelectBody().accept(selectDeParser);
    }

    @Override
    public void visit(Truncate truncate) {
        buffer.append("TRUNCATE TABLE ");
        buffer.append(truncate.getTable());
        if (truncate.getCascade()) {
            buffer.append(" CASCADE");
        }
    }

    @Override
    public void visit(Update update) {
        selectDeParser.setBuffer(buffer);
        expressionDeParser.setSelectVisitor(selectDeParser);
        expressionDeParser.setBuffer(buffer);
        UpdateDeParser updateDeParser = new UpdateDeParser(expressionDeParser, selectDeParser, buffer);
        selectDeParser.setExpressionVisitor(expressionDeParser);
        updateDeParser.deParse(update);

    }

    public StringBuilder getBuffer() {
        return buffer;
    }

    public void setBuffer(StringBuilder buffer) {
        this.buffer = buffer;
    }

    @Override
    public void visit(Alter alter) {
        AlterDeParser alterDeParser = new AlterDeParser(buffer);
        alterDeParser.deParse(alter);
    }

    @Override
    public void visit(Statements stmts) {
        stmts.accept(this);
    }

    @Override
    public void visit(Execute execute) {
        selectDeParser.setBuffer(buffer);
        expressionDeParser.setSelectVisitor(selectDeParser);
        expressionDeParser.setBuffer(buffer);
        ExecuteDeParser executeDeParser = new ExecuteDeParser(expressionDeParser, buffer);
        selectDeParser.setExpressionVisitor(expressionDeParser);
        executeDeParser.deParse(execute);
    }

    @Override
    public void visit(SetStatement set) {
        selectDeParser.setBuffer(buffer);
        expressionDeParser.setSelectVisitor(selectDeParser);
        expressionDeParser.setBuffer(buffer);
        SetStatementDeParser setStatementDeparser = new SetStatementDeParser(expressionDeParser, buffer);
        selectDeParser.setExpressionVisitor(expressionDeParser);
        setStatementDeparser.deParse(set);
    }

    @Override
    public void visit(Merge merge) {
        //TODO implementation of a deparser
        buffer.append(merge.toString());
    }

    @Override
    public void visit(Commit commit) {
        buffer.append(commit.toString());
    }

    @Override
    public void visit(Upsert upsert) {
        selectDeParser.setBuffer(buffer);
        expressionDeParser.setSelectVisitor(selectDeParser);
        expressionDeParser.setBuffer(buffer);
        selectDeParser.setExpressionVisitor(expressionDeParser);
        UpsertDeParser upsertDeParser = new UpsertDeParser(expressionDeParser, selectDeParser, buffer);
        upsertDeParser.deParse(upsert);
    }

    @Override
    public void visit(UseStatement use) {
        new UseStatementDeParser(buffer).deParse(use);
    }

    @Override
    public void visit(Block block) {
        buffer.append("BEGIN\n");
        if (block.getStatements() != null) {
            for (Statement stmt : block.getStatements().getStatements()) {
                stmt.accept(this);
                buffer.append(";\n");
            }
        }
        buffer.append("END");
    }

    @Override
    public void visit(Comment comment) {
        buffer.append(comment.toString());
    }
  
    @Override
    public void visit(ValuesStatement values) {
        expressionDeParser.setBuffer(buffer);
        new ValuesStatementDeParser(expressionDeParser, buffer).deParse(values);
    }
}
