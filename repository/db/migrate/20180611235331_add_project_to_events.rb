class AddProjectToEvents < ActiveRecord::Migration[5.1]
  def change
    add_column :events, :project_name, :string
    add_reference :events, :project, foreign_key: true
  end
end
