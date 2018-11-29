using System;
using System.Data;
using System.Windows.Forms;
using System.IO;
using System.Collections;
using Microsoft.VisualBasic;

namespace VirtualMachine
{
    public partial class InterfaceVM : Form
    {
        //Variáveis Globais Para Uso do Passo a Passo
        int topoDaPilhaStep = -99, linhaInstrucaoStep = 0;
        ArrayList arrayStashStep = new ArrayList();
        Instruction instructionStep = new Instruction();
        //Cria a dataTable para adicionarmos colunas e linhas na lista
        DataTable dtStep = new DataTable();

        public InterfaceVM()
        {
            InitializeComponent();
        }

        //Carrega o Arquivo e adiciona ele no dataGrid
        private void arquivoToolStripMenuItem_Click(object sender, EventArgs e)
        {
            //Variaveis Gerais para esta etapa
            int i, linhaNumero = 1;
            string newline;

            //Pega o arquivo
            OpenFileDialog openFile = new OpenFileDialog();

            //Verifica se o arquivo foi selecionado corretamente
            if (openFile.ShowDialog() == DialogResult.OK)
            {
                //Cria a dataTable para adicionarmos os valores na lista
                DataTable dt = new DataTable();

                //Adiciona a tabela ano dataGrid
                dataGridView1.DataSource = dt;

                //Colunas da tabela
                dt.Columns.Add("I");
                dt.Columns.Add("Instrução");
                dt.Columns.Add("Atributo #1");
                dt.Columns.Add("Atributo #2");

                //Preenche as colunas para caber no espaço do dataGrid
                dataGridView1.Columns[0].AutoSizeMode = DataGridViewAutoSizeColumnMode.DisplayedCells;
                dataGridView1.Columns[1].AutoSizeMode = DataGridViewAutoSizeColumnMode.DisplayedCells;
                dataGridView1.Columns[2].AutoSizeMode = DataGridViewAutoSizeColumnMode.DisplayedCells;
                dataGridView1.Columns[3].AutoSizeMode = DataGridViewAutoSizeColumnMode.Fill;

                //Coloca o arquivo em uma variavel
                StreamReader file = new StreamReader(openFile.FileName);

                //Enquanto a tiver linha no arquivo le
                while ((newline = file.ReadLine()) != null)
                {
                    //Cria uma linha para a tabela
                    DataRow dr = dt.NewRow();

                    //Vetor para pegar a linha, separando por espaço
                    string[] values;
                    values = newline.Split(' ');
                    
                    //Adiciona os valores separados por espaço nas colunas
                    for (i = 0; i < values.Length; i++)
                    {
                        dr[0] = linhaNumero;
                        dr[i+1] = values[i];
                    }
                    //Adiciona a linha na tabela
                    dt.Rows.Add(dr);
                    linhaNumero++;
                }

                dataGridView1.Rows[0].Selected = true;
                file.Close();

            }

        }

        //Executa o código direto, sem passar linha a linha
        private void startToolStripMenuItem_Click(object sender, EventArgs e)
        {
            //Cria a dataTable para adicionarmos colunas e linhas na lista
            DataTable dt = new DataTable();
            //Coloca no dataGrid as colunas criadas
            dataGridView2.DataSource = dt;

            //Colunas da tabela
            dt.Columns.Add("Endereço(s):");
            dt.Columns.Add("Valor:");

            //Preenche as colunas para caber no espaço do dataGrid
            dataGridView2.Columns[0].AutoSizeMode = DataGridViewAutoSizeColumnMode.DisplayedCells;
            dataGridView2.Columns[1].AutoSizeMode = DataGridViewAutoSizeColumnMode.Fill;
            
            //Variaveis Gerais
            int linhaInstrucao = 0, i, j = 0;
            int topoDaPilha = 0;
            string instructionName, firstAttribute, secondAttribute;

            ArrayList breakpoint = new ArrayList();

            while (dataGridView3.Rows[j].Cells[0].Value != null)
            {
                breakpoint.Add(dataGridView3.Rows[j].Cells[0].Value.ToString());
                j++;
            }
            breakpoint.Sort();
            
            for (int k = 0; k < (breakpoint.Count - 1); k++)
            {
                if (breakpoint[k].Equals(breakpoint[k + 1]))
                {
                    breakpoint.RemoveAt(k);
                    k--;
                }
            }                

            //Array que guarda os valores da pilha e posição
            ArrayList arrayStash = new ArrayList();
            //Objeto da Classe que contém as instruções que o Compilador faz
            Instruction instruction = new Instruction();

            while (topoDaPilha != -99)
            {

                if (breakpoint.Count > 0 && breakpoint[0].Equals(linhaInstrucao.ToString()))
                {
                    DialogResult dialogResult = MessageBox.Show("Continuar ?", "BreakPoint", MessageBoxButtons.OK);
                    if (dialogResult == DialogResult.OK)
                    {
                        breakpoint.RemoveAt(0);
                    }
                    
                }

                dataGridView1.ClearSelection();

                instructionName = dataGridView1.Rows[linhaInstrucao].Cells[1].Value.ToString();
                firstAttribute = dataGridView1.Rows[linhaInstrucao].Cells[2].Value.ToString();
                secondAttribute = dataGridView1.Rows[linhaInstrucao].Cells[3].Value.ToString();

                if (instructionName.Equals("RETURNF"))
                {
                    // salvo o topo da pilha numa variável
                    String auxStashTop = arrayStash[topoDaPilha].ToString();
                    arrayStash.RemoveAt(topoDaPilha);
                    topoDaPilha--;

                    // realizo o dalloc se houver os dois atributos
                    if (firstAttribute != "" && secondAttribute != "")
                    {
                        topoDaPilha = instruction.execute("DALLOC", "", "", arrayStash, topoDaPilha);
                    }
                    // realizo o return
                    linhaInstrucao = Convert.ToInt32(arrayStash[topoDaPilha]);
                    topoDaPilha = instruction.execute("RETURN", "", "", arrayStash, topoDaPilha);
                    // devolvo o valor da função pro topo da pilha

                    topoDaPilha = instruction.execute("LDC", auxStashTop, "", arrayStash, topoDaPilha);
                }
                if (instructionName.Equals("RETURN"))
                {
                    dataGridView1.ClearSelection();
                    dataGridView1.Rows[linhaInstrucao].Selected = true;
                    linhaInstrucao = Convert.ToInt32(arrayStash[topoDaPilha]);
                }
                if (instructionName.Equals("CALL"))
                {
                    dataGridView1.ClearSelection();
                    dataGridView1.Rows[linhaInstrucao].Selected = true;
                    arrayStash.Add("");
                    topoDaPilha++;
                    //isso é uma autêntica gambiarra
                    secondAttribute = Convert.ToString(linhaInstrucao);
                }

                if (instructionName.Equals("JMP") || instructionName.Equals("JMPF"))
                {
                    int newInstructionLine;
                    if (instructionName.Equals("JMP"))
                    {
                        newInstructionLine = instruction.executeJump(dataGridView1, instructionName, firstAttribute, "");
                    } else {
                        newInstructionLine = instruction.executeJump(dataGridView1, instructionName, firstAttribute, Convert.ToString(arrayStash[topoDaPilha]));
                    }
                    
                    if (newInstructionLine != (-1))
                    {
                        linhaInstrucao = newInstructionLine;
                    }
                    else
                    {
                        //Não encontrou a linha especificada no jump
                        //Verificar criação de exceções para cada tipo de erro possível na máquina virtual
                    }

                    if (instructionName.Equals("JMPF"))
                    {
                        arrayStash.RemoveAt(topoDaPilha);
                        topoDaPilha--;
                    }
                }
                else
                {
                    topoDaPilha = instruction.execute(instructionName, firstAttribute, secondAttribute, arrayStash, topoDaPilha);
                }


                if (!(instructionName.Equals("START")) && !(instructionName.Equals("HLT")) && !(instructionName.Equals("JMP")) && !(instructionName.Equals("JMPF")))
                {
                    dt.Clear();

                    if (instructionName.Equals("RD"))
                    {
                        if (topoDaPilha >= arrayStash.Count)
                        {
                            arrayStash.Add(int.Parse(Interaction.InputBox("Próximo Valor de Entrada:", "Input", "", -1, -1)));
                        }
                        else
                        {
                            arrayStash[topoDaPilha] = int.Parse(Interaction.InputBox("Próximo Valor de Entrada:", "Input", "", -1, -1));
                        }
                        richTextBox3.AppendText(arrayStash[topoDaPilha].ToString() + "\n");
                    }

                    for (i = 0; i < arrayStash.Count; i++)
                    {
                        dt.Rows.Add(i, arrayStash[i].ToString());
                    }

                    if(topoDaPilha >= 0)
                    {
                        dataGridView2.ClearSelection();
                        dataGridView2.Rows[topoDaPilha].Selected = true;
                    }

                    if (instructionName.Equals("PRN"))
                    {
                        richTextBox4.AppendText(arrayStash[topoDaPilha].ToString() + "\n");
                        arrayStash.RemoveAt(topoDaPilha);
                        topoDaPilha--;
                    }

                    if (instructionName.Equals("CALL"))
                    {

                        int newInstructionLine = instructionStep.executeJump(dataGridView1, "JMP", firstAttribute, Convert.ToString(arrayStash[topoDaPilha]));


                        if (newInstructionLine != (-1))
                        {
                            linhaInstrucao = newInstructionLine;
                        }
                        else
                        {
                            //Não encontrou a linha especificada no jump
                            //Verificar criação de exceções para cada tipo de erro possível na máquina virtual
                        }
                        dataGridView1.Rows[linhaInstrucao].Selected = true;
                        linhaInstrucao++;
                    }
                    else
                    {
                        dataGridView1.Rows[linhaInstrucao].Selected = true;
                        if (!instructionName.Equals("RETURN") && !instructionName.Equals("RETURNF"))
                        {
                            linhaInstrucao++;
                        }
                    }

                }
                else
                {
                    linhaInstrucao++;
                }
            }

        }
        
        private void startStepToolStripMenuItem_Click(object sender, EventArgs e)
        {
            //Coloca no dataGrid as colunas criadas
            dataGridView2.DataSource = dtStep;

            //Verificação para caso queira executar o código de novo e não gerar erro de criação de coluna
            if(dtStep.Columns.Count <= 1)
            {
                //Colunas da tabela
                dtStep.Columns.Add("Endereço(s):");
                dtStep.Columns.Add("Valor:");

                //Preenche as colunas para caber no espaço do dataGrid
                dataGridView2.Columns[0].AutoSizeMode = DataGridViewAutoSizeColumnMode.DisplayedCells;
                dataGridView2.Columns[1].AutoSizeMode = DataGridViewAutoSizeColumnMode.Fill;
            }

            string instructionName = dataGridView1.Rows[linhaInstrucaoStep].Cells[1].Value.ToString();
            string firstAttribute = dataGridView1.Rows[linhaInstrucaoStep].Cells[2].Value.ToString();
            string secondAttribute = dataGridView1.Rows[linhaInstrucaoStep].Cells[3].Value.ToString();

            if (dataGridView1.Rows[linhaInstrucaoStep].Cells[1].Value.ToString().Equals("START"))
            {
                dtStep.Clear();
                dataGridView1.ClearSelection();
                dataGridView1.Rows[linhaInstrucaoStep].Selected = true;

                richTextBox3.Clear();
                richTextBox4.Clear();

                topoDaPilhaStep = instructionStep.execute(instructionName, firstAttribute, secondAttribute, arrayStashStep, topoDaPilhaStep);
                linhaInstrucaoStep++;
            }
    
        }

        private void continueToolStripMenuItem_Click(object sender, EventArgs e)
        {
            //Coloca no dataGrid as colunas criadas
            dataGridView2.DataSource = dtStep;

            //Variaveis Gerais
            int i;
            string instructionName, firstAttribute, secondAttribute;

            if (topoDaPilhaStep != -99)
            {

                instructionName = dataGridView1.Rows[linhaInstrucaoStep].Cells[1].Value.ToString();
                firstAttribute = dataGridView1.Rows[linhaInstrucaoStep].Cells[2].Value.ToString();
                secondAttribute = dataGridView1.Rows[linhaInstrucaoStep].Cells[3].Value.ToString();
                if (instructionName.Equals("RETURNF"))
                {
                    // salvo o topo da pilha numa variável
                    String auxStashTop = arrayStashStep[topoDaPilhaStep].ToString();
                    arrayStashStep.RemoveAt(topoDaPilhaStep);
                    topoDaPilhaStep--;

                    // realizo o dalloc se houver os dois atributos
                    if (firstAttribute != "" && secondAttribute != "")
                    {
                        topoDaPilhaStep = instructionStep.execute("DALLOC", "", "", arrayStashStep, topoDaPilhaStep);
                    }
                    // realizo o return
                    linhaInstrucaoStep = Convert.ToInt32(arrayStashStep[topoDaPilhaStep]);
                    topoDaPilhaStep = instructionStep.execute("RETURN", "", "", arrayStashStep, topoDaPilhaStep);
                    // devolvo o valor da função pro topo da pilha

                    topoDaPilhaStep = instructionStep.execute("LDC", auxStashTop, "", arrayStashStep, topoDaPilhaStep);
                }
                if (instructionName.Equals("RETURN"))
                {
                    dataGridView1.ClearSelection();
                    dataGridView1.Rows[linhaInstrucaoStep].Selected = true;
                    linhaInstrucaoStep = Convert.ToInt32(arrayStashStep[topoDaPilhaStep]);
                }
                if (instructionName.Equals("CALL"))
                {
                    dataGridView1.ClearSelection();
                    dataGridView1.Rows[linhaInstrucaoStep].Selected = true;
                    arrayStashStep.Add("");
                    topoDaPilhaStep++;
                    //isso é uma autêntica gambiarra
                    secondAttribute = Convert.ToString(linhaInstrucaoStep);
                }

                if (instructionName.Equals("JMP") || instructionName.Equals("JMPF"))
                {
                    dataGridView1.ClearSelection();
                    dataGridView1.Rows[linhaInstrucaoStep].Selected = true;                    
                    
                        int newInstructionLine;
                        if (topoDaPilhaStep < 0)
                        {
                            newInstructionLine = instructionStep.executeJump(dataGridView1, instructionName, firstAttribute, "");
                        }
                        else
                        {
                            newInstructionLine = instructionStep.executeJump(dataGridView1, instructionName, firstAttribute, Convert.ToString(arrayStashStep[topoDaPilhaStep]));
                        }
                        
                        if (newInstructionLine != (-1))
                        {
                            linhaInstrucaoStep = newInstructionLine;
                        }
                        else
                        {
                        //Não encontrou a linha especificada no jump
                        //Verificar criação de exceções para cada tipo de erro possível na máquina virtual
                        linhaInstrucaoStep++;
                        }

                        if (instructionName.Equals("JMPF"))
                        {
                            arrayStashStep.RemoveAt(topoDaPilhaStep);
                            topoDaPilhaStep--;
                        }

                }
                else
                {
                    topoDaPilhaStep = instructionStep.execute(instructionName, firstAttribute, secondAttribute, arrayStashStep, topoDaPilhaStep);
                }

                if (dataGridView1.Rows[linhaInstrucaoStep].Cells[1].Value.ToString().Equals("HLT"))
                {
                    dataGridView1.ClearSelection();
                    dataGridView1.Rows[linhaInstrucaoStep].Selected = true;
                    topoDaPilhaStep = -99;
                    linhaInstrucaoStep = 0;
                }

                if (!(instructionName.Equals("START")) && !(instructionName.Equals("HLT")) && !(instructionName.Equals("JMP")) && !(instructionName.Equals("JMPF")))
                {
                    dataGridView1.ClearSelection();
                    dtStep.Clear();

                    if (instructionName.Equals("RD"))
                    {
                        if (topoDaPilhaStep >= arrayStashStep.Count)
                        {
                            arrayStashStep.Add(int.Parse(Interaction.InputBox("Próximo Valor de Entrada:", "Input", "", -1, -1)));
                        }
                        else
                        {
                            arrayStashStep[topoDaPilhaStep] = int.Parse(Interaction.InputBox("Próximo Valor de Entrada:", "Input", "", -1, -1));
                        }
                        richTextBox3.AppendText(arrayStashStep[topoDaPilhaStep].ToString() + "\n");
                    }

                    for (i = 0; i < arrayStashStep.Count; i++)
                    {
                        dtStep.Rows.Add(i, arrayStashStep[i].ToString());
                    }

                    dataGridView2.ClearSelection();
                    if(topoDaPilhaStep >= 0)
                    {
                        dataGridView2.Rows[topoDaPilhaStep].Selected = true;
                    }

                    if (instructionName.Equals("PRN"))
                    {
                        richTextBox4.AppendText(arrayStashStep[topoDaPilhaStep].ToString() + "\n");
                        arrayStashStep.RemoveAt(topoDaPilhaStep);
                        topoDaPilhaStep--;
                    }
                    if (instructionName.Equals("CALL"))
                    {
                        
                        int newInstructionLine = instructionStep.executeJump(dataGridView1, "JMP", firstAttribute, Convert.ToString(arrayStashStep[topoDaPilhaStep]));

                        
                        if (newInstructionLine != (-1))
                        {
                            linhaInstrucaoStep = newInstructionLine;
                        }
                        else
                        {
                            //Não encontrou a linha especificada no jump
                            //Verificar criação de exceções para cada tipo de erro possível na máquina virtual
                        }
                        dataGridView1.Rows[linhaInstrucaoStep].Selected = true;
                        linhaInstrucaoStep++;
                    }
                    else
                    {
                        dataGridView1.Rows[linhaInstrucaoStep].Selected = true;
                        if(!instructionName.Equals("RETURN") && !instructionName.Equals("RETURNF"))
                        {
                            linhaInstrucaoStep++;
                        }  
                    }
                }
                
            }

        }

        private void stopToolStripMenuItem_Click(object sender, EventArgs e)
        {
            topoDaPilhaStep = -99;
            linhaInstrucaoStep = 0;

            arrayStashStep.Clear();

            richTextBox3.Clear();
            richTextBox4.Clear();

            dtStep.Clear();
            dataGridView1.ClearSelection();
            dataGridView1.Rows[0].Selected = true;
        }
    }

    public class Instruction
    {
        public int executeJump(DataGridView file, String instruction, String line, String contentTopStack)
        {
            int i = 1;
            if (instruction.Equals("JMP"))
            {
                while (!file.Rows[i].Cells[1].Value.ToString().Equals("HLT"))
                {
                    if (file.Rows[i].Cells[1].Value.ToString().Equals(line))
                    {
                        return i;
                    }
                    i++;
                }
                return -1;
            }
            else if (instruction.Equals("JMPF"))
            {
                if (Convert.ToInt32(contentTopStack).Equals(0))
                {
                    return executeJump(file, "JMP", line, contentTopStack);
                }
                else
                {
                    return -1;
                }
            }

            return -1;

        }

        public int execute(string instruction, string firstAttribute, string secondAttribute, ArrayList array, int topoPilha)
        {
            int x, y, k;
            if (String.IsNullOrEmpty(instruction))
            {
                throw new Exception("Instruction not provided");
            }
            else
            {
                switch (instruction)
                {
                    case "START":
                        topoPilha = -1;
                        return topoPilha;

                    case "LDC":
                        topoPilha++;
                        array.Add(Convert.ToInt32(firstAttribute));
                        return topoPilha;

                    case "LDV":
                        topoPilha++;
                        array.Add(array[Convert.ToInt32(firstAttribute)]);
                        return topoPilha;

                    case "ADD":
                        x = Convert.ToInt32(array[topoPilha]);
                        y = Convert.ToInt32(array[topoPilha - 1]);
                        array[topoPilha - 1] = x + y;
                        array.RemoveAt(topoPilha);
                        return topoPilha - 1;

                    case "SUB":
                        x = Convert.ToInt32(array[topoPilha]);
                        y = Convert.ToInt32(array[topoPilha - 1]);
                        array[topoPilha - 1] = y - x;
                        array.RemoveAt(topoPilha);
                        return topoPilha - 1;

                    case "MULT":
                        x = Convert.ToInt32(array[topoPilha]);
                        y = Convert.ToInt32(array[topoPilha - 1]);
                        array[topoPilha - 1] = x * y;
                        array.RemoveAt(topoPilha);
                        return topoPilha - 1;

                    case "DIVI":
                        x = Convert.ToInt32(array[topoPilha]);
                        y = Convert.ToInt32(array[topoPilha - 1]);
                        array[topoPilha - 1] = x / y;
                        array.RemoveAt(topoPilha);
                        return topoPilha - 1;

                    case "INV":
                        x = Convert.ToInt32(array[topoPilha]);
                        array[topoPilha] = x * (-1);
                        return topoPilha;

                    case "NULL":
                        return topoPilha;

                    case "STR":
                        if(Convert.ToInt32(firstAttribute) >= array.Count)
                        {
                            array.Add(array[topoPilha]);
                        }
                        else
                        {
                            array[Convert.ToInt32(firstAttribute)] = array[topoPilha];
                        }
                        array.RemoveAt(topoPilha);
                        return topoPilha - 1;

                    case "RD":
                        topoPilha = topoPilha + 1;
                        return topoPilha;

                    case "PRN":
                        return topoPilha;

                    case "AND":
                        if(array[topoPilha - 1].Equals(1) && array[topoPilha].Equals(1))
                        {
                            array[topoPilha - 1] = 1;
                        }
                        else
                        {
                            array[topoPilha - 1] = 0;
                        }
                        array.RemoveAt(topoPilha);
                        return topoPilha - 1;

                    case "OR":
                        if (array[topoPilha - 1].Equals(1) || array[topoPilha].Equals(1))
                        {
                            array[topoPilha - 1] = 1;
                        }
                        else
                        {
                            array[topoPilha - 1] = 0;
                        }
                        array.RemoveAt(topoPilha);
                        return topoPilha - 1;

                    case "NEG":
                        array[topoPilha] = 1 - Convert.ToInt32(array[topoPilha]);
                        return topoPilha;

                    case "CME":
                        if (Convert.ToInt32(array[topoPilha - 1]) < Convert.ToInt32(array[topoPilha]))
                        {
                            array[topoPilha - 1] = 1;
                        }
                        else
                        {
                            array[topoPilha - 1] = 0;
                        }
                        array.RemoveAt(topoPilha);
                        return topoPilha - 1;

                    case "CMEQ":
                        if (Convert.ToInt32(array[topoPilha - 1]) <= Convert.ToInt32(array[topoPilha]))
                        {
                            array[topoPilha - 1] = 1;
                        }
                        else
                        {
                            array[topoPilha - 1] = 0;
                        }
                        array.RemoveAt(topoPilha);
                        return topoPilha - 1;

                    case "CMA":
                        if (Convert.ToInt32(array[topoPilha - 1]) > Convert.ToInt32(array[topoPilha]))
                        {
                            array[topoPilha - 1] = 1;
                        }
                        else
                        {
                            array[topoPilha - 1] = 0;
                        }
                        array.RemoveAt(topoPilha);
                        return topoPilha - 1;

                    case "CMAQ":
                        if (Convert.ToInt32(array[topoPilha - 1]) >= Convert.ToInt32(array[topoPilha]))
                        {
                            array[topoPilha - 1] = 1;
                        }
                        else
                        {
                            array[topoPilha - 1] = 0;
                        }
                        array.RemoveAt(topoPilha);
                        return topoPilha - 1;


                    case "CEQ":
                        if (array[topoPilha - 1].Equals(array[topoPilha]))
                        {
                            array[topoPilha - 1] = 1;
                        }
                        else
                        {
                            array[topoPilha - 1] = 0;
                        }
                        array.RemoveAt(topoPilha);
                        return topoPilha - 1;

                    case "CDIF":
                        if (!array[topoPilha - 1].Equals(array[topoPilha]))
                        {
                            array[topoPilha - 1] = 1;
                        }
                        else
                        {
                            array[topoPilha - 1] = 0;
                        }
                        array.RemoveAt(topoPilha);
                        return topoPilha - 1;

                    case "ALLOC":
                        for (k = 0; k < Convert.ToInt32(secondAttribute); k++)
                        {
                            topoPilha = topoPilha + 1;
                            if(topoPilha <= Convert.ToInt32(firstAttribute))
                            {
                                array.Add("");
                            }
                            else
                            {
                                array.Add("");
                                array[topoPilha] = array[(Convert.ToInt32(firstAttribute) + k)];
                            }
                        }
                        return topoPilha;

                    case "DALLOC":
                        for (k = (Convert.ToInt32(secondAttribute) - 1); k >= 0; k--)
                        {
                            array[(Convert.ToInt32(firstAttribute) + k)] = array[topoPilha];
                            array.RemoveAt(topoPilha);
                            topoPilha = topoPilha - 1;
                        }
                        return topoPilha;

                    case "CALL":
                        array[topoPilha] = Convert.ToInt32(secondAttribute) + 1;
                        return topoPilha;

                    case "RETURN":
                        array.RemoveAt(topoPilha);
                        return topoPilha - 1;

                    case "RETURNF":
                        return topoPilha;

                    case "HLT":
                        array.Clear();
                        return -99;

                    default:
                        if (String.Equals(firstAttribute,"NULL"))
                        {
                            topoPilha = execute(firstAttribute, "", "", array, topoPilha);
                            return topoPilha;
                        }
                        return -99;

                }
            }
        }
    }
}
