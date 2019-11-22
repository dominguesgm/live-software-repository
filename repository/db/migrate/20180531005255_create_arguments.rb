class CreateArguments < ActiveRecord::Migration[5.1]
  def change
    create_table :arguments do |t|
      t.string :argument_name
      t.string :type
      t.references :i_method, foreign_key: true

      t.timestamps
    end
  end
end
