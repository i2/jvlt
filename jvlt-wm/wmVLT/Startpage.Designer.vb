<Global.Microsoft.VisualBasic.CompilerServices.DesignerGenerated()> _
Partial Public Class Startpage
    Inherits System.Windows.Forms.Form

    'Das Formular überschreibt den Löschvorgang, um die Komponentenliste zu bereinigen.
    <System.Diagnostics.DebuggerNonUserCode()> _
    Protected Overrides Sub Dispose(ByVal disposing As Boolean)
        If disposing AndAlso components IsNot Nothing Then
            components.Dispose()
        End If
        MyBase.Dispose(disposing)
    End Sub

    'Wird vom Windows Form-Designer benötigt.
    Private components As System.ComponentModel.IContainer
    Private mainMenu1 As System.Windows.Forms.MainMenu

    'Hinweis: Die folgende Prozedur ist für den Windows Form-Designer erforderlich.
    'Das Bearbeiten ist mit dem Windows Form-Designer möglich.  
    'Das Bearbeiten mit dem Code-Editor ist nicht möglich.
    <System.Diagnostics.DebuggerStepThrough()> _
    Private Sub InitializeComponent()
        Dim resources As System.ComponentModel.ComponentResourceManager = New System.ComponentModel.ComponentResourceManager(GetType(Startpage))
        Me.mainMenu1 = New System.Windows.Forms.MainMenu
        Me.menLaden = New System.Windows.Forms.MenuItem
        Me.menAbfrage = New System.Windows.Forms.MenuItem
        Me.menLindner = New System.Windows.Forms.MenuItem
        Me.menStandard = New System.Windows.Forms.MenuItem
        Me.OpenFileDialog1 = New System.Windows.Forms.OpenFileDialog
        Me.lblWelcome = New System.Windows.Forms.Label
        Me.SuspendLayout()
        '
        'mainMenu1
        '
        Me.mainMenu1.MenuItems.Add(Me.menLaden)
        Me.mainMenu1.MenuItems.Add(Me.menAbfrage)
        '
        'menLaden
        '
        Me.menLaden.Text = "Vokabeln laden"
        '
        'menAbfrage
        '
        Me.menAbfrage.Enabled = False
        Me.menAbfrage.MenuItems.Add(Me.menLindner)
        Me.menAbfrage.MenuItems.Add(Me.menStandard)
        Me.menAbfrage.Text = "Abfrage"
        '
        'menLindner
        '
        Me.menLindner.Enabled = False
        Me.menLindner.Text = "Lindner"
        '
        'menStandard
        '
        Me.menStandard.Text = "Standard"
        '
        'OpenFileDialog1
        '
        Me.OpenFileDialog1.FileName = "OpenFileDialog1"
        Me.OpenFileDialog1.Filter = "VLT Dateien (*.jvlt)|*.jvlt"
        '
        'lblWelcome
        '
        Me.lblWelcome.Location = New System.Drawing.Point(16, 25)
        Me.lblWelcome.Name = "lblWelcome"
        Me.lblWelcome.Size = New System.Drawing.Size(205, 215)
        Me.lblWelcome.Text = resources.GetString("lblWelcome.Text")
        Me.lblWelcome.TextAlign = System.Drawing.ContentAlignment.TopCenter
        '
        'Startpage
        '
        Me.AutoScaleDimensions = New System.Drawing.SizeF(96.0!, 96.0!)
        Me.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Dpi
        Me.AutoScroll = True
        Me.ClientSize = New System.Drawing.Size(240, 268)
        Me.Controls.Add(Me.lblWelcome)
        Me.Menu = Me.mainMenu1
        Me.Name = "Startpage"
        Me.Text = "wmVLT"
        Me.ResumeLayout(False)

    End Sub
    Friend WithEvents menLaden As System.Windows.Forms.MenuItem
    Friend WithEvents menAbfrage As System.Windows.Forms.MenuItem
    Friend WithEvents menLindner As System.Windows.Forms.MenuItem
    Friend WithEvents OpenFileDialog1 As System.Windows.Forms.OpenFileDialog
    Friend WithEvents lblWelcome As System.Windows.Forms.Label
    Friend WithEvents menStandard As System.Windows.Forms.MenuItem
End Class
