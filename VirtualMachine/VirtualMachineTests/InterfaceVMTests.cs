using Microsoft.VisualStudio.TestTools.UnitTesting;
using VirtualMachine;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace VirtualMachine.Tests
{
    [TestClass()]
    public class InterfaceVMTests
    {
        [TestMethod()]
        public void InterfaceVMTest()
        {
            InterfaceVM myInterface = new InterfaceVM();
            Instruction instruction = new Instruction("ADD", "", "");

            Assert.IsNotNull(myInterface);
        }
    }
}